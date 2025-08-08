package com.yapp.backend.repository;

import com.yapp.backend.service.model.User;

public interface UserRepository {
    User findByIdOrThrow(Long id);
    User getUserBySocialUserInfoOrCreateUser(User socialUserInfo);
    void deleteById(Long userId);
    void saveUser(User findUser);
}
