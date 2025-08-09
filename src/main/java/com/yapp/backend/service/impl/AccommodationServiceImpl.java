package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.request.UpdateAccommodationRequest;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.controller.dto.response.UserProfileResponse;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.mapper.ScrapingDataMapper;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.UserTripBoard;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.UserTripBoardRepository;
import com.yapp.backend.service.AccommodationService;
import com.yapp.backend.service.ScrapingService;
import com.yapp.backend.service.dto.ScrapingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 숙소 도메인 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

	private final AccommodationRepository accommodationRepository;
	private final UserTripBoardRepository userTripBoardRepository;
	private final ScrapingService scrapingService;
	private final ScrapingDataMapper scrapingDataMapper;

	/**
	 * 숙소 목록 조회
	 * 특정 유저가 생성한 숙소 목록을 조회
	 * userId 가 null인 경우, 그룹에 속한 모든 숙소를 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public AccommodationPageResponse findAccommodationsByBoardId(Long boardId, int page, int size, Long userId,
			String sort) {
		try {
			// size + 1개를 조회하여 다음 페이지 존재 여부를 한 번의 쿼리로 확인
			List<Accommodation> accommodations = accommodationRepository.findByBoardIdWithPagination(
					boardId, page, size + 1, userId, sort);

			// 실제 반환할 데이터와 다음 페이지 존재 여부 판단
			boolean hasNext = accommodations.size() > size;
			List<Accommodation> actualAccommodations = hasNext
					? accommodations.subList(0, size)
					: accommodations;

			List<AccommodationResponse> accommodationResponses = actualAccommodations.stream()
					.map(AccommodationResponse::from)
					.collect(Collectors.toList());

			// 여행보드 참여자 프로필 정보 조회 (실패해도 숙소 목록 조회는 정상 진행)
			List<UserProfileResponse> userProfiles;
			try {
				userProfiles = getUserProfilesByBoardId(boardId);
			} catch (Exception e) {
				log.warn("Failed to fetch user profiles for boardId: {}, proceeding with empty list", boardId, e);
				userProfiles = Collections.emptyList();
			}

			return AccommodationPageResponse.builder()
					.accommodations(accommodationResponses)
					.userProfiles(userProfiles)
					.hasNext(hasNext)
					.build();
		} catch (DataAccessException e) {
			log.error("Database error while finding accommodations for boardId: {}, userId: {}",
					boardId, userId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

	/**
	 * table에 포함된 숙소 카드의 개수 반환
	 */
	@Override
	public Long countAccommodationsByBoardId(Long boardId, Long userId) {
		try {
			return accommodationRepository.countByBoardId(boardId, userId);
		} catch (DataAccessException e) {
			log.error("Database error while counting accommodations for boardId: {}, userId: {}",
					boardId, userId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

	/**
	 * 숙소 카드 등록
	 * URL을 통해 외부 스크래핑 서버에서 숙소 정보를 가져와 등록합니다.
	 * 인증된 사용자 정보를 created_by 필드에 저장합니다.
	 */
	@Override
	public AccommodationRegisterResponse registerAccommodationCard(AccommodationRegisterRequest request, Long userId) {
		try {
			log.info("숙소 등록 시작 - 사용자 ID: {}, 보드 ID: {}, URL: {}", userId, request.getBoardId(), request.getUrl());

			// 외부 스크래핑 서버에서 숙소 정보 가져오기
			ScrapingResponse scrapingResponse = scrapingService.scrapeAccommodationData(request.getUrl());

			// 스크래핑 데이터를 Entity로 매핑 (사용자 ID 포함)
			AccommodationEntity accommodationEntity = scrapingDataMapper.mapToEntity(
					scrapingResponse.getData(),
					request.getUrl(),
					request.getMemo(),
					userId, // 인증된 사용자 ID를 created_by 필드에 저장
					request.getBoardId());

			// 새로운 숙소 카드 등록
			Accommodation savedAccommodation = accommodationRepository.save(accommodationEntity);

			log.info("숙소 등록 완료 - 숙소 ID: {}, 사용자 ID: {}", savedAccommodation.getId(), userId);

			// 응답 객체 생성
			return AccommodationRegisterResponse.builder()
					.accommodationId(savedAccommodation.getId())
					.build();
		} catch (CustomException e) {
			// Re-throw custom exceptions (validation errors) as-is
			log.warn("숙소 등록 실패 - 사용자 ID: {}, 사유: {}", userId, e.getMessage());
			throw e;
		} catch (DataIntegrityViolationException e) {
			log.error("Database constraint violation while registering accommodation - 사용자 ID: {}", userId, e);
			throw new CustomException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION);
		} catch (DataAccessException e) {
			log.error("Database error while registering accommodation - 사용자 ID: {}", userId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		} catch (Exception e) {
			log.error("Unexpected error while registering accommodation - 사용자 ID: {}", userId, e);
			throw new CustomException(ErrorCode.ACCOMMODATION_REGISTRATION_FAILED);
		}
	}

	/**
	 * 숙소 단건 조회
	 * 특정 숙소 ID로 숙소 정보를 조회합니다.
	 */
	@Override
	@Transactional(readOnly = true)
	public AccommodationResponse findAccommodationById(Long accommodationId) {
		try {
			Accommodation accommodation = accommodationRepository.findById(accommodationId);

			if (accommodation == null) {
				throw new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND);
			}

			return AccommodationResponse.from(accommodation);
		} catch (CustomException e) {
			// Re-throw custom exceptions as-is
			throw e;
		} catch (DataAccessException e) {
			log.error("Database error while finding accommodation by id: {}", accommodationId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

	/**
	 * 숙소 정보 업데이트
	 * 숙소의 세부 정보를 업데이트합니다.
	 */
	@Override
	@Transactional
	public void updateAccommodation(UpdateAccommodationRequest request) {
		try {
			// 기존 숙소 조회
			Accommodation existingAccommodation = accommodationRepository.findByIdOrThrow(request.getId());

			// 숙소 정보 업데이트 (도메인 객체 내부에서 부분 업데이트 처리)
			existingAccommodation.update(request);

			// 업데이트된 숙소를 데이터베이스에 저장
			accommodationRepository.update(existingAccommodation);

			log.info("Accommodation updated successfully for id: {}", request.getId());

		} catch (CustomException e) {
			throw e;
		} catch (DataAccessException e) {
			log.error("Database error while updating accommodation id: {}", request.getId(), e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		} catch (Exception e) {
			log.error("Unexpected error while updating accommodation", e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

	/**
	 * 여행보드 참여자 프로필 정보 조회
	 * 여행보드 ID로 해당 보드의 모든 참여자 정보를 조회하여 UserProfileResponse로 변환
	 * 조회 실패 시 빈 리스트를 반환하여 숙소 목록 조회는 정상 진행되도록 함
	 */
	private List<UserProfileResponse> getUserProfilesByBoardId(Long boardId) {
		try {
			log.debug("Fetching user profiles for boardId: {}", boardId);

			List<UserTripBoard> userTripBoards = userTripBoardRepository.findByTripBoardIdWithUser(boardId);

			if (userTripBoards == null || userTripBoards.isEmpty()) {
				log.debug("No participants found for boardId: {}", boardId);
				return Collections.emptyList();
			}

			List<UserProfileResponse> userProfiles = userTripBoards.stream()
					.filter(userTripBoard -> userTripBoard != null && userTripBoard.getUser() != null)
					.map(this::mapToUserProfileResponse)
					.filter(userProfile -> userProfile != null)
					.collect(Collectors.toList());

			log.debug("Successfully fetched {} user profiles for boardId: {}", userProfiles.size(), boardId);
			return userProfiles;

		} catch (DataAccessException e) {
			log.warn("Database error while fetching user profiles for boardId: {}, returning empty list. Error: {}",
					boardId, e.getMessage(), e);
			return Collections.emptyList();
		} catch (Exception e) {
			log.warn("Unexpected error while fetching user profiles for boardId: {}, returning empty list. Error: {}",
					boardId, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	/**
	 * UserTripBoard를 UserProfileResponse로 변환
	 * null 체크를 통해 안전한 변환 수행
	 */
	private UserProfileResponse mapToUserProfileResponse(UserTripBoard userTripBoard) {
		try {
			if (userTripBoard == null || userTripBoard.getUser() == null) {
				log.warn("Invalid UserTripBoard or User data found during mapping, skipping");
				return null;
			}

			return UserProfileResponse.builder()
					.userId(userTripBoard.getUser().getId())
					.nickname(userTripBoard.getUser().getNickname())
					.build();
		} catch (Exception e) {
			log.warn("Error while mapping UserTripBoard to UserProfileResponse: {}", e.getMessage(), e);
			return null;
		}
	}

}
