package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.TripBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface JpaTripBoardRepository extends JpaRepository<TripBoardEntity, Long> {

    /**
     * 여행 보드 ID와 생성자 ID로 여행 보드를 조회합니다. (소유자 검증용)
     */
    @Query("SELECT tb FROM TripBoardEntity tb WHERE tb.id = :tripBoardId AND tb.createdBy.id = :createdById")
    Optional<TripBoardEntity> findByIdAndCreatedById(@Param("tripBoardId") Long tripBoardId,
            @Param("createdById") Long createdById);

    /**
     * 특정 여행보드의 다음 비교표 번호를 조회하고 증가시킵니다 (락 사용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tb FROM TripBoardEntity tb WHERE tb.id = :tripBoardId")
    Optional<TripBoardEntity> getAndIncrementSequenceNumber(@Param("tripBoardId") Long tripBoardId);
}