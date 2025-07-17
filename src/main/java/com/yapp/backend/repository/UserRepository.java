package com.yapp.backend.repository;

import com.yapp.backend.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> getUser(String provider, String socialId);
    User save(User user);
    void deleteById(Long userId);


}
