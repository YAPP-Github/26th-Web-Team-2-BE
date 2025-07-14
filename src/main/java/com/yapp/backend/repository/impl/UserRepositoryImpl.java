package com.yapp.backend.repository.impl;

import com.yapp.backend.domain.entity.User;
import com.yapp.backend.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> findById(Long id) {
        // Mock data
        return Optional.of(new User(id, "testUser"));
    }
}
