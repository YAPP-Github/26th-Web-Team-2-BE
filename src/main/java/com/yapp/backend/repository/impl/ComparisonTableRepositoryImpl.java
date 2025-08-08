package com.yapp.backend.repository.impl;

import com.yapp.backend.common.exception.ComparisonTableNotFoundException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.ComparisonTableRepository;
import com.yapp.backend.repository.JpaAccommodationRepository;
import com.yapp.backend.repository.JpaComparisonTableRepository;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.entity.ComparisonAccommodationEntity;
import com.yapp.backend.repository.entity.ComparisonTableEntity;
import com.yapp.backend.repository.mapper.ComparisonTableMapper;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ComparisonTableRepositoryImpl implements ComparisonTableRepository {

    private final JpaComparisonTableRepository jpaComparisonTableRepository;
    private final JpaAccommodationRepository jpaAccommodationRepository;
    private final AccommodationRepository accommodationRepository;
    private final ComparisonTableMapper comparisonTableMapper;

    @Override
    @Transactional
    public Long save(ComparisonTable comparisonTable) {
        // 비교표 저장
        return jpaComparisonTableRepository
                .save(comparisonTableMapper.domainToEntity(comparisonTable))
                .getId();
    }

    @Override
    public ComparisonTable findByIdOrThrow(Long tableId) {
        ComparisonTableEntity comparisonTableEntity = jpaComparisonTableRepository.findById(tableId)
                .orElseThrow(() -> new ComparisonTableNotFoundException(ErrorCode.TABLE_NOT_FOUND));
        return comparisonTableMapper.entityToDomain(comparisonTableEntity);
    }

    @Override
    @Transactional
    public void update(ComparisonTable comparisonTable) {
        // 기존 엔티티 조회
        ComparisonTableEntity existingEntity = jpaComparisonTableRepository.findById(comparisonTable.getId())
                .orElseThrow(() -> new ComparisonTableNotFoundException(ErrorCode.TABLE_NOT_FOUND));

        // 기본 정보, 숙소 매핑 정보 업데이트
        existingEntity.update(comparisonTable);
        updateAccommodationMappings(existingEntity, comparisonTable.getAccommodationList());
        jpaComparisonTableRepository.save(existingEntity);
    }

    /**
     * 숙소 매핑 정보를 업데이트합니다.
     * 기존 매핑을 삭제/생성하는 대신 position을 업데이트하고 필요한 경우에만 추가/삭제합니다.
     */
    private void updateAccommodationMappings(
            ComparisonTableEntity tableEntity,
            List<Accommodation> accommodationList) {
        // 기존 매핑을 Map으로 변환 (accommodationId -> ComparisonAccommodationEntity)
        Map<Long, ComparisonAccommodationEntity> existingMappings = new HashMap<>();
        for (ComparisonAccommodationEntity item : tableEntity.getItems()) {
            existingMappings.put(item.getAccommodationEntity().getId(), item);
        }

        // 새로운 매핑 리스트 생성
        List<ComparisonAccommodationEntity> updatedItems = new ArrayList<>();
        for (int i = 0; i < accommodationList.size(); i++) {
            Long accommodationId = accommodationList.get(i).getId();
            // 기존 매핑이 있는 경우 position만 업데이트
            if (existingMappings.containsKey(accommodationId)) {
                ComparisonAccommodationEntity existingItem = existingMappings.get(accommodationId);
                ComparisonAccommodationEntity updatedItem = ComparisonAccommodationEntity.builder()
                        .id(existingItem.getId())
                        .comparisonTableEntity(tableEntity)
                        .accommodationEntity(existingItem.getAccommodationEntity())
                        .position(i)
                        .build();
                updatedItems.add(updatedItem);
                existingMappings.remove(accommodationId); // 처리된 매핑 제거
            } else {
                // 새로운 매핑 생성 - AccommodationRepository에서 Entity 조회
                AccommodationEntity accommodationEntity = jpaAccommodationRepository.getReferenceById(accommodationId);
                ComparisonAccommodationEntity newItem = ComparisonAccommodationEntity.builder()
                        .comparisonTableEntity(tableEntity)
                        .accommodationEntity(accommodationEntity)
                        .position(i)
                        .build();
                updatedItems.add(newItem);
            }
        }

        // 매핑 리스트 교체
        // 남은 기존 매핑들은 자동으로 삭제됨 (orphanRemoval = true)
        tableEntity.getItems().clear();
        tableEntity.getItems().addAll(updatedItems);
    }

    @Override
    @Transactional
    public ComparisonTable addAccommodationsToTable(Long tableId, List<Long> accommodationIds, Long userId) {
        // 기존 비교표 조회
        ComparisonTableEntity tableEntity = jpaComparisonTableRepository.findById(tableId)
                .orElseThrow(() -> new ComparisonTableNotFoundException(ErrorCode.TABLE_NOT_FOUND));

        // 권한 검증
        if (!tableEntity.getCreatedByEntity().getId().equals(userId)) {
            throw new UserAuthorizationException();
        }

        // 기존 숙소 ID 목록 추출 (중복 방지용)
        Set<Long> existingAccommodationIds = tableEntity.getItems().stream()
                .map(item -> item.getAccommodationEntity().getId())
                .collect(java.util.stream.Collectors.toSet());

        // 중복되지 않는 새로운 숙소 ID들만 필터링
        List<Long> newAccommodationIds = accommodationIds.stream()
                .filter(id -> !existingAccommodationIds.contains(id))
                .toList();

        // 새로운 숙소 ID들이 실제 DB에 존재하는지 검증
        List<Accommodation> validatedAccommodations = newAccommodationIds.stream()
                .map(accommodationRepository::findByIdOrThrow) // AccommodationNotFoundException 발생 가능
                .toList();

        // 새로운 매핑 엔티티들 생성 및 추가
        int currentMaxPosition = tableEntity.getItems().size();
        for (int i = 0; i < validatedAccommodations.size(); i++) {
            Long accommodationId = validatedAccommodations.get(i).getId();
            AccommodationEntity accommodationEntity = jpaAccommodationRepository.getReferenceById(accommodationId);

            ComparisonAccommodationEntity newMapping = ComparisonAccommodationEntity.builder()
                    .comparisonTableEntity(tableEntity)
                    .accommodationEntity(accommodationEntity)
                    .position(currentMaxPosition + i)
                    .build();

            tableEntity.getItems().add(newMapping);
        }

        jpaComparisonTableRepository.save(tableEntity);
        return comparisonTableMapper.entityToDomain(tableEntity);
    }

    @Override
    @Transactional
    public void deleteByTripBoardIdAndCreatedById(Long tripBoardId, Long createdById) {
        jpaComparisonTableRepository.deleteByTripBoardIdAndCreatedByEntityId(tripBoardId, createdById);
    }

    @Override
    public void deleteByTripBoardId(Long tripBoardId) {
        jpaComparisonTableRepository.deleteByTripBoardEntityId(tripBoardId);
    }
}