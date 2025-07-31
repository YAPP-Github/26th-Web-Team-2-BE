package com.yapp.backend.repository.mapper;

import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.service.model.TripBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper utility for converting between TripBoardEntity and TripBoard domain
 * model
 */
@Component
@RequiredArgsConstructor
public class TripBoardMapper {

    private final UserMapper userMapper;

    /**
     * TripBoardEntity -> TripBoard 도메인 모델 변환
     */
    public TripBoard entityToDomain(TripBoardEntity entity) {
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
     * TripBoard 도메인 모델 -> TripBoardEntity 변환
     */
    public TripBoardEntity domainToEntity(TripBoard tripBoard) {
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
}