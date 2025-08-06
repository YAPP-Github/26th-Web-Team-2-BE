package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.response.AuthorizeUrlResponse;
import com.yapp.backend.controller.dto.response.OauthLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "OAUTH API", description = "소셜 로그인 API")
public interface OauthDocs {

    @Operation(
            summary = "카카오 OAuth 인가 URL 조회",
            description = "카카오 OAuth 인가 페이지 URL을 반환합니다. 클라이언트의 baseUrl을 기반으로 동적으로 redirect_uri를 생성합니다. " +
                         "프론트엔드에서 이 URL로 리다이렉트하여 사용자 인증을 진행합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "카카오 OAuth 인가 URL이 성공적으로 반환됩니다."
    )
    ResponseEntity<StandardResponse<AuthorizeUrlResponse>> getKakaoAuthorizeUrl(
            @RequestParam("baseUrl") String baseUrl
    );

    @Operation(
            summary = "카카오 OAuth 토큰 교환",
            description = "카카오에서 발급받은 인가 코드를 통해 액세스 토큰을 획득하고, 사용자 정보를 조회하여 JWT 토큰을 헤더로 설정합니다. " +
                         "JWT 토큰은 응답 헤더로 전달되며, 응답 바디에는 사용자 정보만 포함됩니다. " +
                         "인가 코드와 baseUrl은 Query Parameter로 전달해야 합니다. " +
                         "로그인 성공 후 응답 헤더의 ACCESS_TOKEN, 쿠키에 REFRESH_TOKEN이 전달됩니다."
    )
    @ApiResponse(
            responseCode = "200", 
            description = "JWT 토큰이 쿠키로 성공적으로 설정되고, 사용자 정보가 반환됩니다."
    )
    ResponseEntity<StandardResponse<OauthLoginResponse>> exchangeKakaoToken(
            @RequestParam("code") String code,
            @RequestParam("baseUrl") String baseUrl,
            @Parameter(hidden = true) HttpServletResponse response
    );

}
