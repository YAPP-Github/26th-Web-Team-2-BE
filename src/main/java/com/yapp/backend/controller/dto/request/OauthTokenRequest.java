package com.yapp.backend.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "OAuth 토큰 요청")
public record OauthTokenRequest(
        @Schema(description = "카카오에서 발급받은 인가 코드")
        @NotBlank(message = "인가 코드는 필수입니다.")
        String code
) {}