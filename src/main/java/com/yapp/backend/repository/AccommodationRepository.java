package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.AccommodationEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<AccommodationEntity, Long> {
}
