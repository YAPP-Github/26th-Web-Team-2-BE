package com.yapp.backend.controller.dto.response;

import com.yapp.backend.common.util.DateUtil;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.UserTripBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardCreateResponse {
    private Long boardId;
    private String boardName;
    private String destination;
    private String travelPeriod; // "25.08.14~08.16" 형식
    private String startDate; // "2025.08.02" 형식
    private String endDate; // "2025.08.02" 형식
    private String invitationUrl;
    private Boolean invitationActive;
    private UserInfo creator;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String email;
        private String profileImage;
    }

    /**
     * TripBoard와 UserTripBoard 도메인 모델로부터 TripBoardCreateResponse를 생성하는 정적 팩토리 메서드
     */
    public static TripBoardCreateResponse from(TripBoard tripBoard, UserTripBoard userTripBoard) {
        return TripBoardCreateResponse.builder()
                .boardId(tripBoard.getId())
                .boardName(tripBoard.getBoardName())
                .destination(tripBoard.getDestination())
                .travelPeriod(DateUtil.formatTravelPeriod(tripBoard.getStartDate(), tripBoard.getEndDate()))
                .startDate(DateUtil.formatFullDate(tripBoard.getStartDate()))
                .endDate(DateUtil.formatFullDate(tripBoard.getEndDate()))
                .invitationUrl(userTripBoard.getInvitationUrl())
                .invitationActive(userTripBoard.getInvitationActive())
                .creator(UserInfo.builder()
                        .id(tripBoard.getCreatedBy().getId())
                        .nickname(tripBoard.getCreatedBy().getNickname())
                        .email(tripBoard.getCreatedBy().getEmail())
                        .profileImage(tripBoard.getCreatedBy().getProfileImage())
                        .build())
                .createdAt(tripBoard.getCreatedAt())
                .build();
    }
}