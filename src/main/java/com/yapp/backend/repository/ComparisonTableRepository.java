package com.yapp.backend.repository;

import com.yapp.backend.service.model.ComparisonTable;

public interface ComparisonTableRepository {
    Long save(ComparisonTable comparisonTable);

    ComparisonTable findByIdOrThrow(Long tableId);

    void update(ComparisonTable comparisonTable);

    /**
     * 비교표에 새로운 숙소들을 추가합니다 (매핑 테이블에만 추가)
     */
    ComparisonTable addAccommodationsToTable(Long tableId, java.util.List<Long> accommodationIds, Long userId);

    /**
     * 여행보드 ID로 해당 보드의 모든 비교표를 삭제합니다.
     */
    void deleteByTripBoardId(Long tripBoardId);
}