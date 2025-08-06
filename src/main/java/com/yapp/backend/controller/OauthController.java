package com.yapp.backend.controller;

import static com.yapp.backend.common.response.ResponseType.*;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.controller.docs.OauthDocs;
import com.yapp.backend.filter.service.RefreshTokenService;
import com.yapp.backend.controller.dto.response.AuthorizeUrlResponse;
import com.yapp.backend.controller.dto.response.OauthLoginResponse;
import com.yapp.backend.service.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OauthController implements OauthDocs {

    private final OauthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 카카오 OAuth 인가 URL을 반환합니다.
     *
     * @return 카카오 OAuth 인가 URL
     */
    @Override
    @GetMapping("/kakao/authorize")
    public ResponseEntity<StandardResponse<AuthorizeUrlResponse>> getKakaoAuthorizeUrl(
            @RequestParam("baseUrl") String baseUrl) {
        String authorizeUrl = oauthService.generateAuthorizeUrl("kakao", baseUrl);
        AuthorizeUrlResponse response = new AuthorizeUrlResponse(authorizeUrl);
        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, response));
    }

    /**
     * 카카오 인가 코드를 통해 토큰을 교환하고 JWT를 쿠키로 설정합니다.
     * Request Body 또는 Query Parameter 모두 지원합니다.
     *
     * @param code 인가 코드 Query Parameter
     * @param baseUrl 클라이언트의 베이스 URL (토큰 교환 시 redirect_uri 생성용)
     * @param response HTTP 응답 (쿠키 설정용)
     * @return 사용자 정보 응답 (토큰은 쿠키로 전달)
     */
    @Override
    @PostMapping("/kakao/token")
    public ResponseEntity<StandardResponse<OauthLoginResponse>> exchangeKakaoToken(
            @RequestParam("code") String code,
            @RequestParam("baseUrl") String baseUrl,
            HttpServletResponse response) {
        
        // 1. OAuth 인증 처리 및 사용자 정보 조회
        OauthLoginResponse oauthResponse = oauthService.exchangeCodeForToken("kakao", code, baseUrl);
        
        // 2. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(oauthResponse.userId());
        String refreshToken = jwtTokenProvider.createRefreshToken(oauthResponse.userId());
        
        // 3. Redis에 Refresh Token 저장
        refreshTokenService.storeRefresh(oauthResponse.userId(), refreshToken);
        
        // 4. HTTP 응답에 토큰 설정 (Access Token은 헤더, Refresh Token은 쿠키)
        response.setHeader("ACCESS_TOKEN", accessToken);
        ResponseCookie refreshCookie = jwtTokenProvider.generateRefreshTokenCookie(oauthResponse.userId());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        
        // 5. 사용자 정보만 응답 바디로 반환 (토큰은 헤더로 전달됨)
        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, oauthResponse));
    }
    
}