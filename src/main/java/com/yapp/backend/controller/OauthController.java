package com.yapp.backend.controller;

import static com.yapp.backend.common.response.ResponseType.*;
import static com.yapp.backend.common.util.CookieUtil.REFRESH_TOKEN_COOKIE;
import static com.yapp.backend.filter.JwtFilter.ACCESS_TOKEN_HEADER;
import static com.yapp.backend.common.util.TokenUtil.extractTokenFromHeader;

import com.google.common.net.HttpHeaders;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.common.util.CookieUtil;
import com.yapp.backend.controller.docs.OauthDocs;
import com.yapp.backend.controller.dto.response.LogoutResponse;
import com.yapp.backend.filter.service.RefreshTokenService;
import com.yapp.backend.controller.dto.response.AuthorizeUrlResponse;
import com.yapp.backend.controller.dto.response.OauthLoginResponse;
import com.yapp.backend.controller.dto.response.WithdrawResponse;
import com.yapp.backend.service.OauthService;
import com.yapp.backend.filter.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import com.yapp.backend.service.UserService;
import com.yapp.backend.filter.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OauthController implements OauthDocs {

    private final OauthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;
    private final UserService userService;

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

        // TODO: access, refresh 생성 메서드 재활용 가능하도록 리팩토링
        // 2. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(oauthResponse.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(oauthResponse.getUserId());
        
        // 3. Redis에 Refresh Token 저장
        refreshTokenService.storeRefresh(oauthResponse.getUserId(), refreshToken);

        // 4. 사용자 정보만 응답 바디로 반환 (토큰은 헤더로 전달됨)
        oauthResponse.deliverToken(accessToken, refreshToken);
        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, oauthResponse));
    }

    /**
     * 사용자 로그아웃 API
     * Redis에서 Refresh Token을 삭제하고 Access Token을 블랙리스트에 추가하며 쿠키를 무효화합니다.
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param request HTTP 요청 (Access Token 추출용)
     * @param response HTTP 응답 (쿠키 삭제용)
     * @return 로그아웃 성공 여부
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @PostMapping("/logout")
    public ResponseEntity<StandardResponse<LogoutResponse>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        Long userId = userDetails.getUserId();

        // 모든 토큰 무효화 (Refresh Token 삭제 + Access Token 블랙리스트)
        String accessToken = extractTokenFromHeader(request);
        refreshTokenService.logoutUser(userId, accessToken);

        // 쿠키 무효화
        ResponseCookie invalidatedCookie = cookieUtil.createInvalidatedCookie(REFRESH_TOKEN_COOKIE);
        response.addHeader(HttpHeaders.SET_COOKIE, invalidatedCookie.toString());

        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, new LogoutResponse(true)));
    }

    /**
     * 회원탈퇴 API
     * 사용자 데이터를 Soft Delete 처리합니다.
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param request HTTP 요청 (Access Token 추출용)
     * @param response HTTP 응답 (쿠키 삭제용)
     * @return 회원탈퇴 응답 객체
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @PostMapping("/withdraw")
    public ResponseEntity<StandardResponse<WithdrawResponse>> withdrawUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            HttpServletResponse response) {

        Long userId = userDetails.getUserId();

        // 1. 회원탈퇴 처리 (Soft Delete)
        Boolean isWithdraw = userService.withdrawUser(userId);

        // 2. 모든 토큰 무효화 (로그아웃과 동일)
        String accessToken = extractTokenFromHeader(request);
        refreshTokenService.logoutUser(userId, accessToken);

        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, new WithdrawResponse(isWithdraw)));
    }

}