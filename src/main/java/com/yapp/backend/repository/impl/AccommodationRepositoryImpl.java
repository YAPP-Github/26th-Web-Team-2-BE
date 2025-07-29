package com.yapp.backend.repository.impl;

import com.yapp.backend.common.exception.AccommodationNotFoundException;
import com.yapp.backend.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.JpaAccommodationRepository;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.enums.SortType;
import com.yapp.backend.repository.mapper.AccommodationMapper;
import com.yapp.backend.service.model.Accommodation;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AccommodationRepositoryImpl implements AccommodationRepository {
	private final JpaAccommodationRepository jpaAccommodationRepository;
	private final AccommodationMapper accommodationMapper;

	/**
	 * 테이블 ID로 숙소 목록을 페이징하여 조회하는 쿼리
	 * userId가 null이 아닌 경우 해당 사용자가 생성한 숙소만 조회
	 * sort 파라미터에 따라 정렬 방식 결정 (saved_at_desc: 최근 등록순, price_asc: 가격 낮은 순)
	 */
	@Override
	public List<Accommodation> findByTableIdWithPagination(Long tableId, int page, int size, Long userId, String sort) {
		Pageable pageable = PageRequest.of(page, size);
		Page<AccommodationEntity> entityPage;

		// 정렬 방식에 따른 쿼리 선택
		SortType sortType = SortType.fromString(sort);
		boolean isPriceSort = sortType == SortType.PRICE_ASC;

		entityPage = userId != null
			? getEntityPageWithUserId(tableId, userId, pageable, isPriceSort)
			: getEntityPageWithoutUserId(tableId, pageable, isPriceSort);

		return entityPage.getContent().stream()
			.map(this::convertToAccommodation)
			.collect(Collectors.toList());
	}

	/**
	 * 테이블 ID로 숙소 개수를 조회
	 * userId가 null이 아닌 경우 해당 사용자가 생성한 숙소 개수만 조회
	 */
	@Override
	public Long countByTableId(Long tableId, Long userId) {
		if (userId != null) {
			return jpaAccommodationRepository.countByTableIdAndCreatedBy(tableId, userId);
		} else {
			return jpaAccommodationRepository.countByTableId(tableId);
		}
	}

	/**
	 * 숙소를 저장합니다.
	 */
	@Override
	public Accommodation save(AccommodationEntity accommodationEntity) {
		AccommodationEntity savedEntity = jpaAccommodationRepository.save(accommodationEntity);
		return convertToAccommodation(savedEntity);
	}

	@Override
	public Accommodation findByIdOrThrow(Long accommodationId) {
		AccommodationEntity accommodationEntity = jpaAccommodationRepository.findById(accommodationId)
				.orElseThrow(() -> new AccommodationNotFoundException(ErrorCode.ACCOMMODATION_NOT_FOUND));
		return accommodationMapper.entityToDomain(accommodationEntity);
	}

	/**
	 * 숙소 ID로 단건 조회합니다.
	 */
	@Override
	public Accommodation findById(Long accommodationId) {
		AccommodationEntity entity = jpaAccommodationRepository.findByAccommodationId(accommodationId);
		return entity != null ? convertToAccommodation(entity) : null;
	}

	/**
	 * userId가 있는 경우의 엔티티 페이지 조회
	 */
	private Page<AccommodationEntity> getEntityPageWithUserId(Long tableId, Long userId, Pageable pageable,
		boolean isPriceSort) {
		return isPriceSort
			? jpaAccommodationRepository.findByTableIdAndCreatedByOrderByLowestPriceAsc(tableId, userId, pageable)
			: jpaAccommodationRepository.findByTableIdAndCreatedByOrderByCreatedAtDesc(tableId, userId, pageable);
	}

	/**
	 * userId가 없는 경우의 엔티티 페이지 조회
	 */
	private Page<AccommodationEntity> getEntityPageWithoutUserId(Long tableId, Pageable pageable, boolean isPriceSort) {
		return isPriceSort
			? jpaAccommodationRepository.findByTableIdOrderByLowestPriceAsc(tableId, pageable)
			: jpaAccommodationRepository.findByTableIdOrderByCreatedAtDesc(tableId, pageable);
	}

	/**
	 * Convert AccommodationEntity to Accommodation domain model
	 */
	private Accommodation convertToAccommodation(AccommodationEntity entity) {
		return accommodationMapper.entityToDomain(entity);
	}

}