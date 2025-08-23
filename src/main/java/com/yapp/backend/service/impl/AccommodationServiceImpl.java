package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.request.UpdateAccommodationRequest;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.controller.dto.response.AccommodationDeleteResponse;
import com.yapp.backend.controller.dto.response.AccommodationMemoUpdateResponse;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.mapper.ScrapingDataMapper;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.ComparisonTableRepository;
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
import java.util.stream.Collectors;

/**
 * 숙소 도메인 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final ComparisonTableRepository comparisonTableRepository;
    private final ScrapingService scrapingService;
    private final ScrapingDataMapper scrapingDataMapper;

	/**
	 * 숙소 목록 조회
	 * 특정 유저가 생성한 숙소 목록을 조회
	 * userId 가 null인 경우, 그룹에 속한 모든 숙소를 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public AccommodationPageResponse findAccommodationsByTripBoardId(Long tripBoardId, int page, int size, Long userId,
																	 String sort) {
		try {
			// size + 1개를 조회하여 다음 페이지 존재 여부를 한 번의 쿼리로 확인
			List<Accommodation> accommodations = accommodationRepository.findByTripBoardIdWithPagination(
					tripBoardId, page, size + 1, userId, sort);

			// 실제 반환할 데이터와 다음 페이지 존재 여부 판단
			boolean hasNext = accommodations.size() > size;
			List<Accommodation> actualAccommodations = hasNext
					? accommodations.subList(0, size)
					: accommodations;

			List<AccommodationResponse> accommodationResponses = actualAccommodations.stream()
					.map(AccommodationResponse::from)
					.collect(Collectors.toList());

			return AccommodationPageResponse.builder()
					.accommodations(accommodationResponses)
					.hasNext(hasNext)
					.build();
		} catch (DataAccessException e) {
			log.error("Database error while finding accommodations for tripBoardId: {}, userId: {}",
					tripBoardId, userId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

	/**
	 * table에 포함된 숙소 카드의 개수 반환
	 */
	@Override
	public Long countAccommodationsByTripBoardId(Long tripBoardId, Long userId) {
		try {
			return accommodationRepository.countByTripBoardId(tripBoardId, userId);
		} catch (DataAccessException e) {
			log.error("Database error while counting accommodations for tripBoardId: {}, userId: {}",
					tripBoardId, userId, e);
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
			log.info("숙소 등록 시작 - 사용자 ID: {}, 보드 ID: {}, URL: {}", userId, request.getTripBoardId(), request.getUrl());

			// 외부 스크래핑 서버에서 숙소 정보 가져오기
			ScrapingResponse scrapingResponse = scrapingService.scrapeAccommodationData(request.getUrl());

			// 스크래핑 데이터를 Entity로 매핑 (사용자 ID 포함)
			AccommodationEntity accommodationEntity = scrapingDataMapper.mapToEntity(
					scrapingResponse.getData(),
					request.getUrl(),
					request.getMemo(),
					userId, // 인증된 사용자 ID를 created_by 필드에 저장
					request.getTripBoardId());

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
            Accommodation accommodation = accommodationRepository.findByIdOrThrow(accommodationId);

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
	 * 숙소 메모 업데이트
	 * 특정 숙소의 메모만을 업데이트합니다.
	 */
	@Override
	@Transactional
	public AccommodationMemoUpdateResponse updateAccommodationMemo(Long accommodationId, String memo) {
		try {
			log.info("숙소 메모 업데이트 시작 - 숙소 ID: {}, 메모: {}", accommodationId, memo);

			// 숙소 존재 여부 확인
			Accommodation accommodation = accommodationRepository.findByIdOrThrow(accommodationId);

			// 업데이트된 도메인 객체를 저장
			accommodationRepository.updateMemoById(accommodationId, memo);

			log.info("숙소 메모 업데이트 완료 - 숙소 ID: {}", accommodationId);

			// 업데이트된 숙소 정보 다시 조회하여 응답 생성
			Accommodation updatedAccommodation = accommodationRepository.findByIdOrThrow(accommodationId);

			return AccommodationMemoUpdateResponse.of(
					updatedAccommodation.getId(),
					updatedAccommodation.getMemo(),
					updatedAccommodation.getUpdatedAt());

		} catch (CustomException e) {
			// Re-throw custom exceptions as-is
			throw e;
		} catch (DataAccessException e) {
			log.error("Database error while updating accommodation memo for id: {}", accommodationId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		} catch (Exception e) {
			log.error("Unexpected error while updating accommodation memo for id: {}", accommodationId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

    /**
     * 숙소 삭제
     * 본인이 등록한 숙소만 삭제할 수 있으며, 관련 비교표 데이터도 함께 정리됩니다.
     */
    @Override
    @Transactional
    public AccommodationDeleteResponse deleteAccommodation(Long accommodationId, Long userId) {
        try {
            log.info("숙소 삭제 요청 - 사용자ID: {}, 숙소ID: {}", userId, accommodationId);

            // 숙소 존재 여부 확인
            Accommodation accommodation = accommodationRepository.findByIdOrThrow(accommodationId);

            // 소유자 권한 검증 (createdBy 필드 확인)
            if (!accommodation.getCreatedBy().equals(userId)) {
                log.warn("숙소 삭제 권한 없음 - 사용자ID: {}, 숙소ID: {}", userId, accommodationId);
                throw new CustomException(ErrorCode.ACCOMMODATION_DELETE_FORBIDDEN);
            }

            // 관련 비교표 데이터 정리 (숙소가 포함된 모든 비교표에서 해당 숙소 제거)
            comparisonTableRepository.removeAccommodationFromAllTables(accommodationId);
            log.info("비교표에서 숙소 참조 제거 완료 - 숙소ID: {}", accommodationId);

            // 숙소 삭제 실행
            accommodationRepository.deleteById(accommodationId);

            log.info("숙소 삭제 완료 - 사용자ID: {}, 숙소ID: {}", userId, accommodationId);

            // 응답 객체 생성
            return AccommodationDeleteResponse.builder()
                    .accommodationId(accommodationId)
                    .build();

        } catch (CustomException e) {
            // Re-throw custom exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            log.error("숙소 삭제 실패 - 사용자ID: {}, 숙소ID: {}, 오류: {}", userId, accommodationId, e.getMessage());
            throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            log.error("숙소 삭제 중 예상치 못한 오류 - 사용자ID: {}, 숙소ID: {}", userId, accommodationId, e);
            throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
        }
    }
}
