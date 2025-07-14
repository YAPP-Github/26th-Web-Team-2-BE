package com.yapp.backend.service.impl;

import com.yapp.backend.domain.entity.UserEntity;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.UserService;
import com.yapp.backend.service.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return User.from(userEntity);
    }
}
