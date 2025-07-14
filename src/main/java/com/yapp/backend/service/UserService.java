package com.yapp.backend.service;

import com.yapp.backend.domain.dto.UserDto;

public interface UserService {
    UserDto getUserById(Long id);
}
