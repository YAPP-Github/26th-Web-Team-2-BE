package com.yapp.backend.controller.dto.response;

import com.yapp.backend.repository.enums.TripBoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 여행 보드 참여자 프로필 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantProfileResponse {
    private Long userId;
    private String profileImageUrl;
    private String nickname;
    private TripBoardRole role;
}