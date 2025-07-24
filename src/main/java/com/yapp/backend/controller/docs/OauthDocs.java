package com.yapp.backend.controller.docs;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "OAUTH API", description = "소셜 로그인 API")
public interface OauthDocs {

    @Operation(
            summary = "카카오 소셜 로그인 리다이렉션",
            description = "클라이언트 요청 시 카카오 OAuth2 인가 페이지로 리다이렉트하고, 인가 완료 시 쿠키에 ACCESS TOKEN 및 REFRESH TOKEN을 발급합니다."
    )
    @ApiResponse(
            responseCode = "302",
            description = "카카오 인가 페이지로 리다이렉트됩니다. 인가 성공 시 쿠키에 ACCESS TOKEN과 REFRESH TOKEN이 설정됩니다."
    )
    void redirectToKakaoAuthorization(HttpServletResponse response) throws IOException;
}
