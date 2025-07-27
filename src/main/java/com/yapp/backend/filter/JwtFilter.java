package com.yapp.backend.filter;

import static com.yapp.backend.common.util.CookieUtil.getCookieValue;

import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.filter.service.AuthContextService;
import com.yapp.backend.filter.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

    // 스킵할 URI(인증이 필요 없는 엔드포인트)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        return uri.startsWith("/oauth2/authorization")
                || uri.startsWith("/oauth2/authorize")
                || uri.startsWith("/login/oauth2/code")
                || uri.equals("/")
                || uri.startsWith("/error")
                || uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/api/oauth")
                ;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = getCookieValue(request, "ACCESS_TOKEN");
        try {
            // CASE 1: Valid Access Token
            jwtTokenProvider.validateAccessTokenOrThrow(accessToken);
            authContextService.createAuthContext(accessToken);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException bad) {
            // CASE 2: InValid Access Token
            String refreshToken = getCookieValue(request, "REFRESH_TOKEN");
            if (validateRefreshToken(refreshToken)) {
                Long userId = Long.valueOf(jwtTokenProvider.getRefreshUsername(refreshToken));
                updateAccessAndRefreshToken(request, response, userId);
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
            HttpServletRequest request,
            HttpServletResponse response,
            Long userId
    ) {
        // 1) 새 토큰 생성
        String newAccess = jwtTokenProvider.createAccessToken(userId);
        String newRefresh = jwtTokenProvider.createRefreshToken(userId);

        // 2) Redis Refresh Token 회전
        refreshTokenService.rotateRefresh(userId, newRefresh);

        // 3) 새로운 쿠키 세팅
        ResponseCookie accessCookie = jwtTokenProvider.generateAccessTokenCookie(userId);
        ResponseCookie refreshCookie = jwtTokenProvider.generateRefreshTokenCookie(userId);
        response.setHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 4) 인증 객체 세팅
        authContextService.createAuthContext(newAccess);
    }
}