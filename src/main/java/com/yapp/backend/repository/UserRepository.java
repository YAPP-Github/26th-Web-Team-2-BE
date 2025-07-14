package com.yapp.backend.repository;

import com.yapp.backend.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
}
