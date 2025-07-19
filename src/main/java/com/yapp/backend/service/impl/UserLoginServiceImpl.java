package com.yapp.backend.service.impl;

import com.yapp.backend.service.model.User;
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
     * userSocialInfo로 user 정보를 조회하고,
     * 기존에 존재하지 않을 경우 새롭게 생성해 반환합니다.
     * @param userSocialInfo
     * @return
     */
    @Override
    @Transactional
    public User handleOAuthLogin(User userSocialInfo) {
        return userRepository.getUserBySocialUserInfo(userSocialInfo);
    }
}