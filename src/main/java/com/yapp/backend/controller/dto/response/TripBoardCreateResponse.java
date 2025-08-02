package com.yapp.backend.controller.dto.response;

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
}