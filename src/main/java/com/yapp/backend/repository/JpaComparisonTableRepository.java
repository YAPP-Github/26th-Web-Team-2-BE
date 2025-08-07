package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.ComparisonTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaComparisonTableRepository extends JpaRepository<ComparisonTableEntity, Long> {

    /**
     * 여행보드 ID로 해당 보드의 모든 비교표를 삭제합니다.
     */
    void deleteByTripBoardEntityId(Long tripBoardId);
}