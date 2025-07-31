package com.yapp.backend.repository.impl;

import com.yapp.backend.common.exception.ComparisonTableNotFoundException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.repository.ComparisonTableRepository;
import com.yapp.backend.repository.JpaComparisonTableRepository;
import com.yapp.backend.repository.entity.ComparisonTableEntity;
import com.yapp.backend.repository.mapper.ComparisonTableMapper;
import com.yapp.backend.service.model.ComparisonTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ComparisonTableRepositoryImpl implements ComparisonTableRepository {
    
    private final JpaComparisonTableRepository jpaComparisonTableRepository;
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
}