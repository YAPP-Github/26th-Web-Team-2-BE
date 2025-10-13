package com.yapp.backend.filter;

import static com.yapp.backend.common.util.TokenUtil.extractTokenFromHeader;
import static com.yapp.backend.common.util.CookieUtil.getCookieValue;

import com.yapp.backend.common.annotation.PublicApi;
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
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthContextService authContextService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public static final String ACCESS_TOKEN_HEADER = "access-token";
    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 1. 시스템 관련 URI는 기본적으로 스킵 (OAuth2, Swagger, 에러 페이지 등)
        String uri = request.getRequestURI();
        if (isSystemUri(uri)) {
            return true;
        }

        // 2. @PublicApi 어노테이션이 붙은 컨트롤러 메서드는 스킵
        return isPublicApiEndpoint(request);
    }

    /**
     * 시스템 관련 URI 체크 (OAuth2, Swagger, 에러 페이지 등)
     * 이러한 URI들은 Spring Security 또는 시스템에서 자동으로 처리되는 경로들
     */
    private boolean isSystemUri(String uri) {
        return uri.startsWith("/oauth2/authorization")
                || uri.startsWith("/oauth2/authorize")
                || uri.startsWith("/login/oauth2/code")
                || uri.equals("/")
                || uri.startsWith("/error")
                || uri.startsWith("/swagger")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator")
                ;
    }

    /**
     * @PublicApi 어노테이션이 붙은 엔드포인트인지 체크
     */
    private boolean isPublicApiEndpoint(HttpServletRequest request) {
        try {
            // HandlerExecutionChain을 먼저 가져와서 null 체크
            var handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerExecutionChain == null) {
                log.debug("핸들러를 찾을 수 없습니다: {}", request.getRequestURI());
                return false;
            }

            // Handler를 가져와서 null 체크 및 타입 확인
            Object handler = handlerExecutionChain.getHandler();
            if (!(handler instanceof HandlerMethod)) {
                log.debug("HandlerMethod가 아닙니다: {}", request.getRequestURI());
                return false;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 메서드 레벨에서 @PublicApi 어노테이션 체크
            if (handlerMethod.hasMethodAnnotation(PublicApi.class)) {
                log.debug("@PublicApi 어노테이션이 붙은 메서드: {}", handlerMethod.getMethod().getName());
                return true;
            }

            // 클래스 레벨에서 @PublicApi 어노테이션 체크
            if (handlerMethod.getBeanType().isAnnotationPresent(PublicApi.class)) {
                log.debug("@PublicApi 어노테이션이 붙은 클래스: {}", handlerMethod.getBeanType().getSimpleName());
                return true;
            }

        } catch (Exception e) {
            // 핸들러를 찾을 수 없는 경우 (404 등) 인증 필요로 처리
            log.debug("핸들러 조회 중 오류 발생: {} - {}", request.getRequestURI(), e.getMessage());
        }

        return false;
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