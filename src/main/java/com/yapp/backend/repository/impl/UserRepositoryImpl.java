package com.yapp.backend.repository.impl;

import com.yapp.backend.domain.User;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> findById(Long id) {
        // Mock data
        UserEntity userEntity = new UserEntity(id, "testUser");
        return Optional.of(User.from(userEntity));
    }
}
