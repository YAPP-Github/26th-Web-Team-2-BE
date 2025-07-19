package com.yapp.backend.repository;

import com.yapp.backend.service.model.User;

public interface UserRepository {
    User findById(Long id);
    User getUserBySocialUserInfo(User socialUserInfo);
    void deleteById(Long userId);

}
