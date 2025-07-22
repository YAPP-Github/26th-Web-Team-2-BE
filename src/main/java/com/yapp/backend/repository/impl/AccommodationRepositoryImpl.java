package com.yapp.backend.repository.impl;

import org.springframework.stereotype.Repository;

import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.JpaAccommodationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccommodationRepositoryImpl implements AccommodationRepository {
	private final JpaAccommodationRepository jpaAccommodationRepository;
}
