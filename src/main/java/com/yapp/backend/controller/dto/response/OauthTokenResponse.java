package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OAuth 토큰 응답")
public record OauthTokenResponse(
        @Schema(description = "사용자 ID")
        Long userId,
        
        @Schema(description = "액세스 토큰")
        String accessToken,
        
        @Schema(description = "리프레시 토큰")
        String refreshToken
) {}