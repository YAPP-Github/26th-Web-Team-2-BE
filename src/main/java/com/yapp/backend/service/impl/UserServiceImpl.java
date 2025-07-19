package com.yapp.backend.service.impl;

import com.yapp.backend.service.model.User;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
}
