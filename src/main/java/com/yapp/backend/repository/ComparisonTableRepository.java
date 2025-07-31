package com.yapp.backend.repository;

import com.yapp.backend.service.model.ComparisonTable;

public interface ComparisonTableRepository {
    Long save(ComparisonTable comparisonTable);

    ComparisonTable findByIdOrThrow(Long tableId);
    
    void update(ComparisonTable comparisonTable);
} 