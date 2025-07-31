package com.yapp.backend.repository.mapper;

import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.UserTripBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility for converting between UserTripBoardEntity and UserTripBoard
 * domain model
 */
@Component
@RequiredArgsConstructor
public class UserTripBoardMapper {

    private final UserMapper userMapper;

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
                .tripBoard(entityToDomainWithoutParticipants(entity.getTripBoard()))
                .invitationUrl(entity.getInvitationUrl())
                .invitationActive(entity.getInvitationActive())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * TripBoardEntity -> TripBoard 도메인 모델 변환 (participants 제외)
     * 순환 참조 방지를 위해 사용
     */
    private TripBoard entityToDomainWithoutParticipants(TripBoardEntity entity) {
        if (entity == null) {
            return null;
        }

        return TripBoard.builder()
                .id(entity.getId())
                .boardName(entity.getBoardName())
                .createdBy(userMapper.entityToDomain(entity.getCreatedBy()))
                .updatedBy(userMapper.entityToDomain(entity.getUpdatedBy()))
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
                .tripBoard(domainToEntityWithoutParticipants(userTripBoard.getTripBoard()))
                .invitationUrl(userTripBoard.getInvitationUrl())
                .invitationActive(userTripBoard.getInvitationActive())
                .role(userTripBoard.getRole())
                .createdAt(userTripBoard.getCreatedAt())
                .updatedAt(userTripBoard.getUpdatedAt())
                .build();
    }

    /**
     * TripBoard 도메인 모델 -> TripBoardEntity 변환 (participants 제외)
     * 순환 참조 방지를 위해 사용
     */
    private TripBoardEntity domainToEntityWithoutParticipants(TripBoard tripBoard) {
        if (tripBoard == null) {
            return null;
        }

        return TripBoardEntity.builder()
                .id(tripBoard.getId())
                .boardName(tripBoard.getBoardName())
                .createdBy(userMapper.domainToEntity(tripBoard.getCreatedBy()))
                .updatedBy(userMapper.domainToEntity(tripBoard.getUpdatedBy()))
                .createdAt(tripBoard.getCreatedAt())
                .updatedAt(tripBoard.getUpdatedAt())
                .build();
    }

    /**
     * UserTripBoardEntity 리스트 -> UserTripBoard 도메인 모델 리스트 변환
     */
    public List<UserTripBoard> entitiesToDomains(List<UserTripBoardEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }
}