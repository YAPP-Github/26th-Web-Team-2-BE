package com.yapp.backend.service.impl;

import com.yapp.backend.domain.User;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserRepository userRepository;

    /**
     * provider와 socialId로 user 정보를 조회하고,
     * 기존에 존재하지 않을 경우 새롭게 생성해 반환합니다.
     * @param userInfo
     * @return
     */
    @Override
    @Transactional
    public User handleOAuthLogin(User userInfo) {
        return userRepository.getUser(userInfo.getProvider(), userInfo.getSocialId())
                .orElseGet(() -> userRepository.save(userInfo));
    }
}