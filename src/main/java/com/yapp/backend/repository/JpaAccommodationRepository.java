package com.yapp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yapp.backend.repository.entity.AccommodationEntity;

public interface JpaAccommodationRepository extends JpaRepository<AccommodationEntity, Long> {
}
