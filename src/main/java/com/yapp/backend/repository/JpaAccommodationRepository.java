package com.yapp.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yapp.backend.repository.entity.AccommodationEntity;

public interface JpaAccommodationRepository extends JpaRepository<AccommodationEntity, Long> {

    /**
     * Find accommodations by tableId with pagination
     */
    @Query("SELECT a FROM AccommodationEntity a WHERE a.tableId = :tableId ORDER BY a.createdAt DESC")
    Page<AccommodationEntity> findByTableIdOrderByCreatedAtDesc(@Param("tableId") Long tableId, Pageable pageable);

    /**
     * Find accommodations by tableId and userId with pagination
     */
    @Query("SELECT a FROM AccommodationEntity a WHERE a.tableId = :tableId AND a.createdBy = :userId ORDER BY a.createdAt DESC")
    Page<AccommodationEntity> findByTableIdAndCreatedByOrderByCreatedAtDesc(@Param("tableId") Long tableId,
            @Param("userId") Long userId, Pageable pageable);

    /**
     * Count accommodations by tableId
     */
    @Query("SELECT COUNT(a) FROM AccommodationEntity a WHERE a.tableId = :tableId")
    long countByTableId(@Param("tableId") Long tableId);

    /**
     * Count accommodations by tableId and userId
     */
    @Query("SELECT COUNT(a) FROM AccommodationEntity a WHERE a.tableId = :tableId AND a.createdBy = :userId")
    long countByTableIdAndCreatedBy(@Param("tableId") Long tableId, @Param("userId") Long userId);

    /**
     * Find accommodation by id
     */
    @Query("SELECT a FROM AccommodationEntity a WHERE a.id = :id")
    AccommodationEntity findByAccommodationId(@Param("id") Long id);
}
