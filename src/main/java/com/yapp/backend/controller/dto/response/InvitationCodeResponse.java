package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자의 초대 링크 정보 조회 응답 DTO
 */
@Getter
@Schema(description = "사용자의 초대 링크 정보 조회 응답")
@AllArgsConstructor
public class InvitationCodeResponse {
        @Schema(description = "여행 보드 ID", example = "1")
        private Long tripBoardId;
        
        @Schema(description = "초대 링크 활성화 여부", example = "true")
        private Boolean isActive;
        
        @Schema(description = "초대 코드", example = "ABC123DEF")
        private String invitationCode;
}
