package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "OAuth 로그인 응답")
public class OauthLoginResponse {
        
        @Schema(description = "사용자 ID", example = "12345")
        private Long userId;
        
        @Schema(description = "사용자 닉네임", example = "홍길동")
        private String nickname;

        @Schema(description = "사용자 이메일", example = "abc1234@gmail.com")
        private String email;

        @Schema(description = "토큰 정보")
        private TokenSuccessResponse token;

        public OauthLoginResponse(Long userId, String nickname, String email) {
                this.userId = userId;
                this.nickname = nickname;
                this.email = email;
        }

        public void deliverToken(String accessToken, String refreshToken) {
                this.token = new TokenSuccessResponse(accessToken, refreshToken);
        }
}