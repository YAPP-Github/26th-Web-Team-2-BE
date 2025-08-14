package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "토큰 발급 응답")
public class TokenSuccessResponse {
    
    @Schema(description = "액세스 토큰", accessMode = Schema.AccessMode.READ_ONLY, example = "access-token-example")
    private String accessToken;
    
    @Schema(description = "리프레시 토큰", accessMode = Schema.AccessMode.READ_ONLY, example = "refresh-token-example")
    private String refreshToken;
}
