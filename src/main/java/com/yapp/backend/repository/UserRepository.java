package com.yapp.backend.repository;

import com.yapp.backend.service.model.User;

public interface UserRepository {
    /**
     * 탈퇴 여부 고려하지 않고 모든 유저 조회
     * @param id
     * @return
     */
    User findByIdOrThrow(Long id);

    /**
     * 소셜 로그인 정보를 통해
     * 탈퇴하지 않은 활성 유저만 조회, 없으면 새로운 사용자 생성
     * @param socialUserInfo
     * @return
     */
    User getUserBySocialUserInfoOrCreateUser(User socialUserInfo);

    /**
     * 유저 정보 저장
     * @param user
     * @return
     */
    User save(User user);
}
