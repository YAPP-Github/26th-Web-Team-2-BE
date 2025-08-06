package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OAuth 로그인 응답")
public record OauthLoginResponse(
        @Schema(description = "사용자 ID")
        Long userId,
        
        @Schema(description = "사용자 닉네임")
        String nickname
) {}