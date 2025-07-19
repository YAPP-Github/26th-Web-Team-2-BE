package com.yapp.backend.repository.impl;

import static com.yapp.backend.common.exception.ErrorCode.*;

import com.yapp.backend.common.exception.UserNotFoundException;
import com.yapp.backend.domain.User;
import com.yapp.backend.repository.JpaUserRepository;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public User findById(Long id) {
        // Mock data
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .toModel();
    }

    @Override
    public User getUserBySocialUserInfo(User socialUserInfo) {
        return jpaUserRepository
                .findByProviderAndSocialId(socialUserInfo.getProvider(), socialUserInfo.getSocialId())
                .orElseGet(() ->
                        jpaUserRepository.save(UserEntity.from(socialUserInfo)))
                .toModel();
    }

    // TODO: 회원 탈퇴 기능 구현
    @Override
    public void deleteById(Long userId) {

    }

}
