package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 초대 링크 활성화/비활성화 토글 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "초대 링크 활성화/비활성화 토글 응답")
public class InvitationToggleResponse {
        @Schema(description = "여행 보드 ID", example = "1")
        private Long tripBoardId;
        
        @Schema(description = "초대 링크 활성화 여부", example = "true")
        private Boolean isActive;
        
        @Schema(description = "초대 코드", example = "ABC123DEF")
        private String invitationCode;
}
