package com.yapp.backend.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.JpaAccommodationRepository;
import com.yapp.backend.repository.entity.AccommodationEntity;
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
	 */
	@Override
	public List<Accommodation> findByTableIdWithPagination(Long tableId, int page, int size, Long userId) {
		Pageable pageable = PageRequest.of(page, size);
		Page<AccommodationEntity> entityPage;

		if (userId != null) {
			entityPage = jpaAccommodationRepository.findByTableIdAndCreatedByOrderByCreatedAtDesc(tableId, userId,
				pageable);
		} else {
			entityPage = jpaAccommodationRepository.findByTableIdOrderByCreatedAtDesc(tableId, pageable);
		}

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

	/**
	 * Convert AccommodationEntity to Accommodation domain model
	 */
	private Accommodation convertToAccommodation(AccommodationEntity entity) {
		return accommodationMapper.entityToDomain(entity);
	}
}
