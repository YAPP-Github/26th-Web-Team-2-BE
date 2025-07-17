package com.yapp.backend.repository.impl;

import com.yapp.backend.domain.User;
import com.yapp.backend.repository.JpaUserRepository;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findById(Long id) {
        // Mock data
        return jpaUserRepository.findById(id).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> getUser(String provider, String socialId) {
        return jpaUserRepository.findByProviderAndSocialId(provider, socialId)
                .map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(UserEntity.from(user)).toModel();
    }

    @Override
    public void deleteById(Long userId) {

    }

}
