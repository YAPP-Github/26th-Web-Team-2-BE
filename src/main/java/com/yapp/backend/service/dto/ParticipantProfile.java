package com.yapp.backend.service.dto;

import com.yapp.backend.repository.enums.TripBoardRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 참여자 프로필 정보 DTO
 * Repository 계층에서 효율적인 쿼리를 위해 사용됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantProfile {
    private Long tripBoardId;
    private Long userId;
    private String profileImageUrl;
    private TripBoardRole role;
}