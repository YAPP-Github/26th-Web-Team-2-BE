package com.yapp.backend.repository.mapper;

import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.service.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper utility for converting between UserEntity and User domain model
 */
@Component
public class UserMapper {

    /**
     * UserEntity -> User 도메인 모델 변환
     */
    public User entityToDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .provider(entity.getProvider())
                .socialId(entity.getSocialId())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .profileImage(entity.getProfileImage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /**
     * User 도메인 모델 -> UserEntity 변환
     */
    public UserEntity domainToEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserEntity.builder()
                .id(user.getId())
                .provider(user.getProvider())
                .socialId(user.getSocialId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}