package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "토큰 발급 응답")
public class TokenSuccessResponse {
    
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NSIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDAzNjAwfQ.example-access-token-signature")
    private String accessToken;
    
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NSIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAyNTkyMDAwfQ.example-refresh-token-signature")
    private String refreshToken;
}
