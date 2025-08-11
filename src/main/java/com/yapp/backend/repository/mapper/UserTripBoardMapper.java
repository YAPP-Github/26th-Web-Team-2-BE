package com.yapp.backend.repository.mapper;

import com.yapp.backend.common.util.DateUtil;
import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.service.dto.ParticipantProfile;
import com.yapp.backend.service.dto.TripBoardSummary;
import com.yapp.backend.service.model.UserTripBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper utility for converting between UserTripBoardEntity and various
 * models/DTOs
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
                .invitationCode(entity.getInvitationCode())
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
                .invitationCode(userTripBoard.getInvitationCode())
                .invitationActive(userTripBoard.getInvitationActive())
                .role(userTripBoard.getRole())
                .createdAt(userTripBoard.getCreatedAt())
                .updatedAt(userTripBoard.getUpdatedAt())
                .build();
    }

    /**
     * UserTripBoardEntity -> TripBoardSummary 변환
     * Repository 계층에서 Service 계층으로 데이터 전달 시 사용
     */
    public TripBoardSummary entityToTripBoardSummary(UserTripBoardEntity userTripBoard, int accommodationCount) {
        if (userTripBoard == null) {
            return null;
        }

        TripBoardEntity tripBoard = userTripBoard.getTripBoard();
        if (tripBoard == null) {
            return null;
        }

        return TripBoardSummary.builder()
                .tripBoardId(tripBoard.getId())
                .boardName(tripBoard.getBoardName())
                .destination(tripBoard.getDestination())
                .startDate(tripBoard.getStartDate())
                .endDate(tripBoard.getEndDate())
                .travelPeriod(DateUtil.formatTravelPeriod(tripBoard.getStartDate(), tripBoard.getEndDate()))
                .userRole(userTripBoard.getRole())
                .accommodationCount(accommodationCount)
                .createdAt(tripBoard.getCreatedAt())
                .updatedAt(tripBoard.getUpdatedAt())
                .build();
    }

    /**
     * UserTripBoardEntity -> ParticipantProfile 변환
     * 참여자 프로필 정보 조회 시 사용
     */
    public ParticipantProfile entityToParticipantProfile(UserTripBoardEntity userTripBoard) {
        if (userTripBoard == null) {
            return null;
        }

        return new ParticipantProfile(
                userTripBoard.getTripBoard().getId(),
                userTripBoard.getUser().getId(),
                userTripBoard.getUser().getNickname(),
                userTripBoard.getUser().getProfileImage(),
                userTripBoard.getRole());
    }
}