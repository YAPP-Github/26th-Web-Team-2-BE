package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "유저 정보 응답")
public class UserInfoResponse {
    
    @Schema(description = "유저 닉네임", example = "홍길동")
    private String nickname;
    
    @Schema(description = "유저 프로필 이미지 URL", example = "https://example.com/profile/user123.jpg")
    private String profileImageUrl;

    @Schema(description = "사용자 이메일", example = "abc1234@gmail.com")
    private String email;


}
