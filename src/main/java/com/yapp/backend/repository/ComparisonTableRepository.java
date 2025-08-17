package com.yapp.backend.repository;

import com.yapp.backend.service.model.ComparisonTable;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ComparisonTableRepository {

    /**
     * 비교표를 저장하고, 저장한 비교표 도메인을 반환합니다.
     * @param comparisonTable
     * @return
     */
    ComparisonTable save(ComparisonTable comparisonTable);

    ComparisonTable findByIdOrThrow(Long tableId);

    void update(ComparisonTable comparisonTable);

    /**
     * 특정 ID의 비교표를 삭제합니다.
     */
    void deleteById(Long tableId);

    /**
     * 비교표에 새로운 숙소들을 추가합니다 (매핑 테이블에만 추가)
     */
    ComparisonTable addAccommodationsToTable(Long tableId, java.util.List<Long> accommodationIds, Long userId);

    /**
     * 특정 여행보드에서 특정 사용자가 생성한 비교표들을 삭제합니다.
     */
    void deleteByTripBoardIdAndCreatedById(Long tripBoardId, Long createdById);

    /**
     * 특정 여행보드의 모든 비교표를 삭제합니다 (여행보드 완전 삭제용).
     */
    void deleteByTripBoardId(Long tripBoardId);

    /**
     * 특정 숙소가 포함된 모든 비교표 매핑을 삭제합니다.
     */
    void removeAccommodationFromAllTables(Long accommodationId);

    /**
     * 특정 여행보드의 비교표 리스트를 페이지네이션으로 조회합니다.
     * @param tripBoardId 여행보드 ID
     * @param pageable 페이지네이션 정보
     * @return 조회된 비교표 리스트
     */
    List<ComparisonTable> findByTripBoardId(Long tripBoardId, Pageable pageable);
}