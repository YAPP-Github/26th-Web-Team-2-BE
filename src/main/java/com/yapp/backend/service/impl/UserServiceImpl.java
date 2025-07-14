package com.yapp.backend.service.impl;

import com.yapp.backend.domain.dto.UserDto;
import com.yapp.backend.domain.entity.User;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDto(user.getId(), user.getName());
    }
}
