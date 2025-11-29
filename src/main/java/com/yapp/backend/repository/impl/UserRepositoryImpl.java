package com.yapp.backend.repository.impl;

import static com.yapp.backend.common.exception.ErrorCode.*;

import com.yapp.backend.common.exception.UserNotFoundException;
import com.yapp.backend.repository.mapper.UserMapper;
import com.yapp.backend.service.model.User;
import com.yapp.backend.repository.JpaUserRepository;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;


    @Override
    public User findByIdOrThrow(Long id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .toModel();
    }

    @Override
    public Optional<User> getUserBySocialUserInfo(User socialUserInfo) {
        return jpaUserRepository
                .findByProviderAndSocialIdAndDeletedAtIsNull(socialUserInfo.getProvider(), socialUserInfo.getSocialId())
                .map(UserEntity::toModel);
    }


    @Override
    public User save(User user) {
        return jpaUserRepository.save(userMapper.domainToEntity(user))
                .toModel();
    }

}
