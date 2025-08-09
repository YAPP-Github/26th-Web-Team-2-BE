package com.yapp.backend.repository.impl;

import static com.yapp.backend.common.exception.ErrorCode.*;

import com.yapp.backend.common.exception.UserNotFoundException;
import com.yapp.backend.repository.mapper.UserMapper;
import com.yapp.backend.service.model.User;
import com.yapp.backend.repository.JpaUserRepository;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public User findByIdOrThrow(Long id) {
        // TODO : 여행 보드 참여자 리스트를 조회할 때, 문제가 생길 여지가 있는지 확인
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .toModel();
    }

    @Override
    public User getUserBySocialUserInfoOrCreateUser(User socialUserInfo) {
        return jpaUserRepository
                .findByProviderAndSocialId(socialUserInfo.getProvider(), socialUserInfo.getSocialId())
                .filter(u -> u.getDeletedAt() != null)
                .orElseGet(() ->
                        jpaUserRepository.save(UserEntity.from(socialUserInfo)))
                .toModel();
    }

    // TODO: 회원 탈퇴 기능 구현 (hard delete)
    @Override
    public void deleteById(Long userId) {

    }

    @Override
    public User save(User user) {
        jpaUserRepository.save(userMapper.domainToEntity(user));
        return user;
    }

}
