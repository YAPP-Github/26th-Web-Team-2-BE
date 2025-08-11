package com.yapp.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.projection.AccommodationCountPerBoard;

import java.util.List;

public interface JpaAccommodationRepository extends JpaRepository<AccommodationEntity, Long> {

        /**
         * Find accommodations by tripBoard with pagination
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.tripBoardId = :tripBoard ORDER BY a.createdAt DESC")
        Page<AccommodationEntity> findByTripBoardIdOrderByCreatedAtDesc(@Param("tripBoard") Long tripBoard, Pageable pageable);

        /**
         * Find accommodations by tripBoard and userId with pagination
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.tripBoardId = :tripBoard AND a.createdBy.id = :userId ORDER BY a.createdAt DESC")
        Page<AccommodationEntity> findByTripBoardIdAndCreatedByOrderByCreatedAtDesc(@Param("tripBoard") Long tripBoard,
                                                                                    @Param("userId") Long userId, Pageable pageable);

        /**
         * Count accommodations by tripBoard
         */
        @Query("SELECT COUNT(a) FROM AccommodationEntity a WHERE a.tripBoardId = :tripBoard")
        long countByTripBoardId(@Param("tripBoard") Long tripBoard);

        /**
         * Count accommodations by tripBoard and userId
         */
        @Query("SELECT COUNT(a) FROM AccommodationEntity a WHERE a.tripBoardId = :tripBoard AND a.createdBy.id = :userId")
        long countByTripBoardIdAndCreatedBy(@Param("tripBoard") Long tripBoard, @Param("userId") Long userId);

        /**
         * Find accommodation by id
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.id = :id")
        AccommodationEntity findByAccommodationId(@Param("id") Long id);

        /**
         * Find accommodations by tripBoard with pagination, sorted by lowest price
         * ascending
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.tripBoardId = :tripBoard ORDER BY a.lowestPrice ASC")
        Page<AccommodationEntity> findByTripBoardIdOrderByLowestPriceAsc(@Param("tripBoard") Long tripBoard, Pageable pageable);

        /**
         * Find accommodations by tripBoard and userId with pagination, sorted by lowest
         * price ascending
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.tripBoardId = :tripBoard AND a.createdBy.id = :userId ORDER BY a.lowestPrice ASC")
        Page<AccommodationEntity> findByTripBoardIdAndCreatedByOrderByLowestPriceAsc(@Param("tripBoard") Long tripBoard,
                                                                                     @Param("userId") Long userId, Pageable pageable);

        /**
         * 특정 여행보드에서 특정 사용자가 생성한 숙소들을 삭제
         */
        void deleteByTripBoardIdAndCreatedById(Long tripBoard, Long createdById);

        /**
         * 특정 여행보드의 모든 숙소를 삭제 (여행보드 완전 삭제용)
         */
        void deleteByTripBoardId(Long tripBoard);

        /**
         * 여러 여행보드의 숙소 개수를 한 번에 조회 (N+1 쿼리 방지)
         * 
         * @param tripBoards 조회할 여행보드 ID 목록
         * @return 여행보드별 숙소 개수 목록
         */
        @Query("SELECT a.tripBoardId as tripBoardId, COUNT(a) as count " +
                        "FROM AccommodationEntity a " +
                        "WHERE a.tripBoardId IN :tripBoards " +
                        "GROUP BY a.tripBoardId")
        List<AccommodationCountPerBoard> countByTripBoardIds(@Param("tripBoards") List<Long> tripBoards);
}
