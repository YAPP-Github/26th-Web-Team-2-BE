package com.yapp.backend.service.impl;

import static com.yapp.backend.common.exception.ErrorCode.*;

import com.yapp.backend.common.exception.DeletedUserException;
import com.yapp.backend.service.model.User;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId) {
        
        User user = userRepository.findByIdOrThrow(userId);
        
        // 탈퇴된 사용자인지 확인
        if (user.getDeletedAt() != null) {
            throw new DeletedUserException(DELETED_USER_ACCESS);
        }
        
        return user;
    }
    
    @Override
    @Transactional
    public Boolean withdrawUser(Long userId) {
        User findUser = userRepository.findByIdOrThrow(userId);
        // 이미 탈퇴된 사용자인지 확인
        if (findUser.getDeletedAt() != null) {
            throw new DeletedUserException(DELETED_USER_ACCESS);
        }
        // Soft Delete 처리
        findUser.withDraw(LocalDateTime.now());
        userRepository.save(findUser);
        return true;
    }
}
