package com.yapp.backend.repository;

import com.yapp.backend.domain.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findById(Long id);
}
