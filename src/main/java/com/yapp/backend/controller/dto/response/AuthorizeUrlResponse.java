package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "OAuth 인가 URL 응답")
public class AuthorizeUrlResponse {
        @Schema(description = "카카오 OAuth 인가 URL", example = "https://kauth.kakao.com/oauth/authorize?client_id=...")
        private String authorizeUrl;
}