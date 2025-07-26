package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.mapper.ScrapingDataMapper;
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

import java.net.MalformedURLException;
import java.net.URL;
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
	private final ScrapingService scrapingService;
	private final ScrapingDataMapper scrapingDataMapper;

	/**
	 * 숙소 목록 조회
	 * 특정 유저가 생성한 숙소 목록을 조회
	 * userId 가 null인 경우, 그룹에 속한 모든 숙소를 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public AccommodationPageResponse findAccommodationsByTableId(Integer tableId, int page, int size, Long userId) {
		try {
			// size + 1개를 조회하여 다음 페이지 존재 여부를 한 번의 쿼리로 확인
			List<Accommodation> accommodations = accommodationRepository.findByTableIdWithPagination(
					tableId.longValue(), page, size + 1, userId);

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
			log.error("Database error while finding accommodations for tableId: {}, userId: {}", tableId, userId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

	/**
	 * table에 포함된 숙소 카드의 개수 반환
	 */
	@Override
	public Long countAccommodationsByTableId(Long tableId, Long userId) {
		try {
			return accommodationRepository.countByTableId(tableId, userId);
		} catch (DataAccessException e) {
			log.error("Database error while counting accommodations for tableId: {}, userId: {}", tableId, userId, e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		}
	}

	/**
	 * 숙소 카드 등록
	 * URL을 통해 외부 스크래핑 서버에서 숙소 정보를 가져와 등록합니다.
	 */
	@Override
	public AccommodationRegisterResponse registerAccommodationCard(AccommodationRegisterRequest request) {
		try {
			// 외부 스크래핑 서버에서 숙소 정보 가져오기
			ScrapingResponse scrapingResponse = scrapingService.scrapeAccommodationData(request.getUrl());

			// 스크래핑 데이터를 Entity로 매핑
			AccommodationEntity accommodationEntity = scrapingDataMapper.mapToEntity(
					scrapingResponse.getData(),
					request.getUrl(),
					request.getMemo(),
					request.getUserId(),
					request.getTableId());

			// Save new accommodation to database using repository
			Accommodation savedAccommodation = accommodationRepository.save(accommodationEntity);

			// Return proper registration response
			return AccommodationRegisterResponse.builder()
					.accommodationId(savedAccommodation.getId())
					.build();
		} catch (CustomException e) {
			// Re-throw custom exceptions (validation errors) as-is
			throw e;
		} catch (DataIntegrityViolationException e) {
			log.error("Database constraint violation while registering accommodation", e);
			throw new CustomException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION);
		} catch (DataAccessException e) {
			log.error("Database error while registering accommodation", e);
			throw new CustomException(ErrorCode.DATABASE_CONNECTION_ERROR);
		} catch (Exception e) {
			log.error("Unexpected error while registering accommodation", e);
			throw new CustomException(ErrorCode.ACCOMMODATION_REGISTRATION_FAILED);
		}
	}
}
