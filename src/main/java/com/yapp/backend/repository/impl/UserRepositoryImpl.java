package com.yapp.backend.repository.impl;

import com.yapp.backend.domain.entity.UserEntity;
import com.yapp.backend.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<UserEntity> findById(Long id) {
        // Mock data
        return Optional.of(new UserEntity(id, "testUser"));
    }
}
