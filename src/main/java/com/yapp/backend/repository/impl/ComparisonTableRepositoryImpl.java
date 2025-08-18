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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ComparisonTableRepositoryImpl implements ComparisonTableRepository {

    private final JpaComparisonTableRepository jpaComparisonTableRepository;
    private final JpaAccommodationRepository jpaAccommodationRepository;
    private final AccommodationRepository accommodationRepository;
    private final ComparisonTableMapper comparisonTableMapper;

    @Override
    @Transactional
    public ComparisonTable save(ComparisonTable comparisonTable) {
        // 비교표 저장
        ComparisonTableEntity savedComparisonTable = jpaComparisonTableRepository
                .save(comparisonTableMapper.domainToEntity(comparisonTable));
        return comparisonTableMapper.entityToDomain(savedComparisonTable);
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
        // 도메인 객체 유효성 검증
        comparisonTable.validateBeforeSave();
        
        // 기존 엔티티 조회 (managed 상태 유지)
        ComparisonTableEntity existingEntity = jpaComparisonTableRepository.findById(comparisonTable.getId())
                .orElseThrow(() -> new ComparisonTableNotFoundException(ErrorCode.TABLE_NOT_FOUND));
        
        // 1. 새로운 매핑 정보 리스트
        List<ComparisonAccommodationEntity> newMappings = updateAccommodationMappings(
                existingEntity,
                comparisonTable.getAccommodationList()
        );
        // 2. 기존 매핑 정보 삭제
        existingEntity.getItems().clear();
        jpaComparisonTableRepository.flush();

        // 2. 비교테이블 메타 정보 및 숙소 매핑 정보 업데이트
        existingEntity.updateComparisonTableEntity(
                comparisonTable.getTableName(),
                comparisonTable.getFactors(),
                newMappings);

        log.debug("비교 테이블 배치 업데이트 완료 - tableId: {}", comparisonTable.getId());
    }


    /**
     * 숙소 매핑 엔티티 리스트를 생성합니다.
     */
    private List<ComparisonAccommodationEntity> updateAccommodationMappings(
            ComparisonTableEntity existingEntity, 
            List<Accommodation> accommodationList) {
        
        List<ComparisonAccommodationEntity> newMappings = new ArrayList<>();
        Set<Long> processedIds = new HashSet<>(); // 중복 방지
        
        for (int i = 0; i < accommodationList.size(); i++) {
            Long accommodationId = accommodationList.get(i).getId();
            
            // 중복 체크
            if (processedIds.contains(accommodationId)) {
                log.warn("중복된 숙소 ID가 감지되어 건너뜁니다: accommodationId={}, position={}", accommodationId, i);
                continue;
            }
            processedIds.add(accommodationId);
            
            // 새로운 매핑 생성
            AccommodationEntity accommodationEntity = jpaAccommodationRepository.getReferenceById(accommodationId);
            ComparisonAccommodationEntity newItem = ComparisonAccommodationEntity.builder()
                    .comparisonTableEntity(existingEntity)
                    .accommodationEntity(accommodationEntity)
                    .position(i)
                    .build();
            newMappings.add(newItem);
        }
        
        log.debug("숙소 매핑 생성 완료 - tableId: {}, 총 매핑 수: {}, 중복 제거된 숙소 수: {}", 
                existingEntity.getId(), newMappings.size(), 
                accommodationList.size() - processedIds.size());
        
        return newMappings;
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
                .collect(Collectors.toSet());

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
        jpaComparisonTableRepository.deleteByTripBoardEntityIdAndCreatedByEntityId(tripBoardId, createdById);
    }

    @Override
    public void deleteByTripBoardId(Long tripBoardId) {
        jpaComparisonTableRepository.deleteByTripBoardEntityId(tripBoardId);
    }

    @Override
    @Transactional
    public void removeAccommodationFromAllTables(Long accommodationId) {
        jpaComparisonTableRepository.deleteComparisonAccommodationsByAccommodationId(accommodationId);
    }

    @Override
    @Transactional
    public void deleteById(Long tableId) {
        jpaComparisonTableRepository.deleteById(tableId);
    }

    @Override
    public List<ComparisonTable> findByTripBoardId(Long tripBoardId, Pageable pageable) {
        // Pageable 객체의 정렬 기준에 따라 적절한 JPA 메서드 선택
        List<ComparisonTableEntity> entities = selectQueryMethodBySort(tripBoardId, pageable);

        return entities.stream()
                .map(comparisonTableMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    /**
     * Pageable의 Sort 정보를 분석하여 적절한 JPA 쿼리 메서드를 선택합니다.
     * 현재는 최근 수정일 기준 내림차순만 지원하며, 향후 확장 가능합니다.
     */
    private List<ComparisonTableEntity> selectQueryMethodBySort(Long tripBoardId, Pageable pageable) {
        // 현재는 최근 수정일 기준 내림차순만 지원
        Page<ComparisonTableEntity> page = jpaComparisonTableRepository.findByTripBoardEntityIdOrderByUpdatedAtDesc(tripBoardId, pageable);
        return page.getContent();
    }
}