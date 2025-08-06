package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OAuth 인가 URL 응답")
public record AuthorizeUrlResponse(
        @Schema(description = "카카오 OAuth 인가 URL", example = "https://kauth.kakao.com/oauth/authorize?client_id=...")
        String authorizeUrl
) {}