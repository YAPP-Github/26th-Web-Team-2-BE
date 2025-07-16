package com.yapp.backend.service;

import com.yapp.backend.controller.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);
}
