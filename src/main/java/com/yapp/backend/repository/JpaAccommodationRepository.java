package com.yapp.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yapp.backend.repository.entity.AccommodationEntity;

public interface JpaAccommodationRepository extends JpaRepository<AccommodationEntity, Long> {

        /**
         * Find accommodations by boardId with pagination
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.boardId = :boardId ORDER BY a.createdAt DESC")
        Page<AccommodationEntity> findByBoardIdOrderByCreatedAtDesc(@Param("boardId") Long boardId, Pageable pageable);

        /**
         * Find accommodations by boardId and userId with pagination
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.boardId = :boardId AND a.createdBy.id = :userId ORDER BY a.createdAt DESC")
        Page<AccommodationEntity> findByBoardIdAndCreatedByOrderByCreatedAtDesc(@Param("boardId") Long boardId,
                        @Param("userId") Long userId, Pageable pageable);

        /**
         * Count accommodations by boardId
         */
        @Query("SELECT COUNT(a) FROM AccommodationEntity a WHERE a.boardId = :boardId")
        long countByBoardId(@Param("boardId") Long boardId);

        /**
         * Count accommodations by boardId and userId
         */
        @Query("SELECT COUNT(a) FROM AccommodationEntity a WHERE a.boardId = :boardId AND a.createdBy.id = :userId")
        long countByBoardIdAndCreatedBy(@Param("boardId") Long boardId, @Param("userId") Long userId);

        /**
         * Find accommodation by id
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.id = :id")
        AccommodationEntity findByAccommodationId(@Param("id") Long id);

        /**
         * Find accommodations by boardId with pagination, sorted by lowest price
         * ascending
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.boardId = :boardId ORDER BY a.lowestPrice ASC")
        Page<AccommodationEntity> findByBoardIdOrderByLowestPriceAsc(@Param("boardId") Long boardId, Pageable pageable);

        /**
         * Find accommodations by boardId and userId with pagination, sorted by lowest
         * price ascending
         */
        @Query("SELECT a FROM AccommodationEntity a WHERE a.boardId = :boardId AND a.createdBy.id = :userId ORDER BY a.lowestPrice ASC")
        Page<AccommodationEntity> findByBoardIdAndCreatedByOrderByLowestPriceAsc(@Param("boardId") Long boardId,
                        @Param("userId") Long userId, Pageable pageable);
}
