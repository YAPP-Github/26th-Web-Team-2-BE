package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "OAuth 로그인 응답")
public class OauthLoginResponse {
        private Long userId;
        private String nickname;
        private TokenSuccessResponse token;

        public OauthLoginResponse(Long userId, String nickname) {
                this.userId = userId;
                this.nickname = nickname;
        }

        public void deliverToken(String accessToken, String refreshToken) {
                this.token = new TokenSuccessResponse(accessToken, refreshToken);
        }
}