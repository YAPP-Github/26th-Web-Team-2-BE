package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.ComparisonTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaComparisonTableRepository extends JpaRepository<ComparisonTableEntity, Long> {
} 