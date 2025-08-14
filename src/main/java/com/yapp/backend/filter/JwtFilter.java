package com.yapp.backend.filter;

import static com.yapp.backend.common.util.TokenUtil.extractTokenFromHeader;
import static com.yapp.backend.common.util.CookieUtil.getCookieValue;

import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.filter.service.AuthContextService;
import com.yapp.backend.filter.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthContextService authContextService;
    private final RefreshTokenService refreshTokenService;

    public static final String ACCESS_TOKEN_HEADER = "access-token";
    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

    // 스킵할 URI(인증이 필요 없는 엔드포인트)

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/oauth2/authorization")
                || uri.startsWith("/oauth2/authorize")
                || uri.startsWith("/login/oauth2/code")
                || uri.equals("/")
                || uri.startsWith("/error")
                || uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/api/comparison/factors")
                || uri.startsWith("/api/comparison/amenity")
                || uri.startsWith("/api/oauth/kakao")
                || uri.startsWith("/api/oauth/refresh")
                ;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 Access Token 추출
        String accessToken = extractTokenFromHeader(request);
        try {
            // CASE 1: Valid Access Token
            jwtTokenProvider.validateAccessTokenOrThrow(accessToken);

            // 블랙리스트 확인
            if (refreshTokenService.isAccessTokenBlacklisted(accessToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }
            authContextService.createAuthContext(accessToken);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException bad) {
            // CASE 2: InValid Access Token - Refresh Token은 쿠키에서 추출 (보안상)
            String refreshToken = getCookieValue(request, REFRESH_TOKEN_COOKIE);
            if (validateRefreshToken(refreshToken)) {
                Long userId = Long.valueOf(jwtTokenProvider.getRefreshUsername(refreshToken));
                updateAccessAndRefreshToken(response, userId);
                filterChain.doFilter(request, response);
                return;
            }
            // CASE 3: InValid Refresh Token
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private boolean validateRefreshToken(String refreshToken) {
        Claims refreshClaims = jwtTokenProvider.parseAndValidateRefreshToken(refreshToken);
        if (refreshClaims == null) {
            log.info("invalid refresh");
            return false;
        }

        // Redis에 저장된 토큰과 일치하는지 검증 -> 일치하지 않으면 401
        Long userId = Long.valueOf(jwtTokenProvider.getRefreshUsername(refreshToken));
        if (!refreshTokenService.isValidRefresh(userId, refreshToken)) {
            log.info("invalid refresh");
            return false;
        }
        return true;
    }

    private void updateAccessAndRefreshToken(
            HttpServletResponse response,
            Long userId) {
        // 1) 새 토큰 생성
        String newAccess = jwtTokenProvider.createAccessToken(userId);
        String newRefresh = jwtTokenProvider.createRefreshToken(userId);

        // 2) Redis Refresh Token 회전
        refreshTokenService.rotateRefresh(userId, newRefresh);

        // 3) 인증 객체 세팅
        authContextService.createAuthContext(newAccess);
    }
}