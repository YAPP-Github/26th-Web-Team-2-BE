package com.yapp.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenSuccessResponse {
    private String accessToken;
    private String refreshToken;
}
