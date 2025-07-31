package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.TripBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTripBoardRepository extends JpaRepository<TripBoardEntity, Long> {
}