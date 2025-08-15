package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.ComparisonTableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaComparisonTableRepository extends JpaRepository<ComparisonTableEntity, Long> {

    /**
     * 특정 여행보드에서 특정 사용자가 생성한 비교표들을 삭제
     */
    void deleteByTripBoardEntityIdAndCreatedByEntityId(Long tripBoardId, Long createdById);

    /**
     * 특정 여행보드의 모든 비교표를 삭제 (여행보드 완전 삭제용)
     */
    void deleteByTripBoardEntityId(Long tripBoardId);

    /**
     * 특정 숙소가 포함된 모든 비교표 매핑을 삭제합니다.
     */
    @Modifying
    @Query("DELETE FROM ComparisonAccommodationEntity ca WHERE ca.accommodationEntity.id = :accommodationId")
    void deleteComparisonAccommodationsByAccommodationId(@Param("accommodationId") Long accommodationId);

    /**
     * 특정 여행보드의 비교표 리스트를 페이지네이션으로 조회합니다. (최근 수정일 내림차순)
     */
    @Query("SELECT c FROM ComparisonTableEntity c WHERE c.tripBoardEntity.id = :tripBoardId ORDER BY c.updatedAt DESC")
    Page<ComparisonTableEntity> findByTripBoardEntityIdOrderByUpdatedAtDesc(@Param("tripBoardId") Long tripBoardId, Pageable pageable);

}