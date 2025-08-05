package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.request.OauthTokenRequest;
import com.yapp.backend.controller.dto.response.AuthorizeUrlResponse;
import com.yapp.backend.controller.dto.response.OauthTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "OAUTH API", description = "소셜 로그인 API - 확장 가능한 어댑터 패턴 구조")
public interface OauthDocs {

    @Operation(
            summary = "카카오 OAuth 인가 URL 조회",
            description = "카카오 OAuth 인가 페이지 URL을 반환합니다. 프론트엔드에서 이 URL로 리다이렉트하여 사용자 인증을 진행합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "카카오 OAuth 인가 URL이 성공적으로 반환됩니다."
    )
    ResponseEntity<StandardResponse<AuthorizeUrlResponse>> getKakaoAuthorizeUrl();

    @Operation(
            summary = "카카오 OAuth 토큰 교환",
            description = "카카오에서 발급받은 인가 코드를 통해 액세스 토큰을 획득하고, 사용자 정보를 조회하여 JWT 토큰을 발급합니다."
    )
    @ApiResponse(
            responseCode = "200", 
            description = "JWT 토큰이 성공적으로 발급됩니다."
    )
    ResponseEntity<StandardResponse<OauthTokenResponse>> exchangeKakaoToken(@RequestBody OauthTokenRequest request);

}
