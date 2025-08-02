package com.yapp.backend.repository.mapper;

import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.service.model.UserTripBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper utility for converting between UserTripBoardEntity and UserTripBoard
 * domain model
 */
@Component
@RequiredArgsConstructor
public class UserTripBoardMapper {

    private final UserMapper userMapper;
    private final TripBoardMapper tripBoardMapper;

    /**
     * UserTripBoardEntity -> UserTripBoard 도메인 모델 변환
     */
    public UserTripBoard entityToDomain(UserTripBoardEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserTripBoard.builder()
                .id(entity.getId())
                .user(userMapper.entityToDomain(entity.getUser()))
                .tripBoard(tripBoardMapper.entityToDomain(entity.getTripBoard()))
                .invitationUrl(entity.getInvitationUrl())
                .invitationActive(entity.getInvitationActive())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * UserTripBoard 도메인 모델 -> UserTripBoardEntity 변환
     */
    public UserTripBoardEntity domainToEntity(UserTripBoard userTripBoard) {
        if (userTripBoard == null) {
            return null;
        }

        return UserTripBoardEntity.builder()
                .id(userTripBoard.getId())
                .user(userMapper.domainToEntity(userTripBoard.getUser()))
                .tripBoard(tripBoardMapper.domainToEntity(userTripBoard.getTripBoard()))
                .invitationUrl(userTripBoard.getInvitationUrl())
                .invitationActive(userTripBoard.getInvitationActive())
                .role(userTripBoard.getRole())
                .createdAt(userTripBoard.getCreatedAt())
                .updatedAt(userTripBoard.getUpdatedAt())
                .build();
    }
}