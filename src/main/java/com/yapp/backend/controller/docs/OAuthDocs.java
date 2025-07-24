package com.yapp.backend.controller.docs;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "OAUTH API", description = "소셜 로그인 API")
public interface OAuthDocs {

    @Operation(summary = "KAKAO", description = "KAKAO 소셜 로그인 요청 URI")
    @ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 리다이렉트")
    void kakaoLoginRedirect(HttpServletResponse response) throws IOException;
}
