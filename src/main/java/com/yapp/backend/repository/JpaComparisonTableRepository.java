package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.ComparisonTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaComparisonTableRepository extends JpaRepository<ComparisonTableEntity, Long> {

    /**
     * 특정 여행보드에서 특정 사용자가 생성한 비교표들을 삭제
     */
    void deleteByTripBoardIdAndCreatedByEntityId(Long tripBoardId, Long createdById);

    /**
     * 특정 여행보드의 모든 비교표를 삭제 (여행보드 완전 삭제용)
     */
    void deleteByTripBoardId(Long tripBoardId);
}

    /**
     * 여행보드 ID로 해당 보드의 모든 비교표를 삭제합니다.
     */
    void deleteByTripBoardEntityId(Long tripBoardId);
}