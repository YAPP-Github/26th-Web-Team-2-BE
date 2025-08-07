package com.yapp.backend.common.util;

import static com.yapp.backend.filter.JwtFilter.REFRESH_TOKEN_COOKIE;

import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.filter.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT 토큰 생성, 검증, 쿠키 관리를 담당하는 유틸리티 클래스
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final long MILLISECONDS_PER_SECOND = 1_000L;
    private static final String COOKIE_PATH = "/";
    private static final String COOKIE_SAME_SITE = "Lax";

    @Value("${spring.security.jwt.access-token-validity-in-ms}")
    private long accessTokenValidityInMs;

    @Value("${spring.security.jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidityInMs;

    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;
    private final RefreshTokenService refreshTokenService;

    public JwtTokenProvider(
            @Value("${spring.security.jwt.access-secret-key}") String accessKey,
            @Value("${spring.security.jwt.refresh-secret-key}") String refreshKey,
            RefreshTokenService refreshTokenService
    ) {
        this.accessSecretKey = createSecretKey(accessKey);
        this.refreshSecretKey = createSecretKey(refreshKey);
        this.refreshTokenService = refreshTokenService;
    }

    // ==================== 토큰 생성 메서드 ====================

    /**
     * Access Token을 생성합니다.
     *
     * @param userId 사용자 ID
     * @return JWT Access Token
     */
    public String createAccessToken(Long userId) {
        validateUserId(userId);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidityInMs);
        
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(accessSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token을 생성합니다.
     *
     * @param userId 사용자 ID
     * @return JWT Refresh Token
     */
    public String createRefreshToken(Long userId) {
        validateUserId(userId);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenValidityInMs);
        
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(refreshSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ==================== 쿠키 생성 메서드 ====================

    /**
     * Refresh Token 쿠키를 생성하고 Redis에 저장합니다.
     *
     * @param userId 사용자 ID
     * @return Refresh Token ResponseCookie
     */
    public ResponseCookie generateRefreshTokenCookie(Long userId) {
        String refreshToken = createRefreshToken(userId);
        long maxAgeInSeconds = refreshTokenValidityInMs / MILLISECONDS_PER_SECOND;
        
        // Redis에 Refresh Token 저장
        refreshTokenService.storeRefresh(userId, refreshToken);
        
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(COOKIE_PATH)
                .maxAge(maxAgeInSeconds)
                .sameSite(COOKIE_SAME_SITE)
                .build();
    }

    // ==================== 토큰 검증 메서드 ====================

    /**
     * Access Token의 유효성을 검증합니다.
     * 토큰이 유효하지 않거나 만료된 경우 예외를 발생시킵니다.
     *
     * @param token 검증할 Access Token
     * @throws JwtException 토큰이 유효하지 않거나 만료된 경우
     */
    public void validateAccessTokenOrThrow(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("Access token is null or empty");
        }

        try {
            Claims claims = getAccessClaims(token);
            if (claims.getSubject() == null || claims.getSubject().trim().isEmpty()) {
                throw new JwtException("Access token subject is missing");
            }
            
            if (isExpired(claims)) {
                throw new ExpiredJwtException(null, claims, "Access token has expired");
            }
        } catch (ExpiredJwtException e) {
            log.debug("Access token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid access token: {}", e.getMessage());
            throw new JwtException("Invalid access token", e);
        }
    }

    /**
     * Refresh Token을 파싱하고 검증합니다.
     *
     * @param token 검증할 Refresh Token
     * @return 유효한 경우 Claims, 그렇지 않으면 null
     */
    public Claims parseAndValidateRefreshToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.debug("Refresh token is null or empty");
            return null;
        }

        try {
            Claims claims = getRefreshClaims(token);
            if (isExpired(claims)) {
                log.debug("Refresh token expired");
                return null;
            }
            return claims;
        } catch (ExpiredJwtException e) {
            log.debug("Refresh token expired: {}", e.getMessage());
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid refresh token: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 토큰 정보 추출 메서드 ====================

    /**
     * Access Token에서 사용자 ID를 추출합니다.
     *
     * @param token Access Token
     * @return 사용자 ID
     * @throws JwtException 토큰이 유효하지 않은 경우
     */
    public String getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("Token is null or empty");
        }
        
        try {
            Claims claims = getAccessClaims(token);
            String subject = claims.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                throw new JwtException("Token subject is missing");
            }
            return subject;
        } catch (JwtException e) {
            log.debug("Failed to extract user ID from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Refresh Token에서 사용자 ID를 추출합니다.
     *
     * @param token Refresh Token
     * @return 사용자 ID
     * @throws JwtException 토큰이 유효하지 않은 경우
     */
    public String getRefreshUsername(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("Refresh token is null or empty");
        }
        
        try {
            Claims claims = getRefreshClaims(token);
            String subject = claims.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                throw new JwtException("Refresh token subject is missing");
            }
            return subject;
        } catch (JwtException e) {
            log.debug("Failed to extract user ID from refresh token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * JWT 토큰과 소셜 ID로 UserDetails 객체를 생성합니다.
     *
     * @param token 인증 토큰
     * @param socialId 소셜 로그인 ID
     * @return CustomUserDetails 객체
     */
    public UserDetails createUserDetails(String token, String socialId) {
        String userId = getUserIdFromToken(token);
        return new CustomUserDetails(
                Long.valueOf(userId),
                socialId,
                List.of() // 권한은 필요에 따라 추가
        );
    }


    // ==================== Private 헬퍼 메서드 ====================

    /**
     * Base64로 인코딩된 시크릿 키로부터 SecretKey 객체를 생성합니다.
     *
     * @param encodedKey Base64로 인코딩된 시크릿 키
     * @return SecretKey 객체
     */
    private SecretKey createSecretKey(String encodedKey) {
        if (encodedKey == null || encodedKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        
        try {
            String cleanedKey = encodedKey.replaceAll("\\s+", "");
            byte[] keyBytes = Decoders.BASE64.decode(cleanedKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid secret key format", e);
        }
    }

    /**
     * 사용자 ID의 유효성을 검증합니다.
     *
     * @param userId 검증할 사용자 ID
     * @throws IllegalArgumentException 사용자 ID가 유효하지 않은 경우
     */
    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }

    /**
     * 토큰의 만료 여부를 확인합니다.
     *
     * @param claims JWT Claims 객체
     * @return 만료된 경우 true, 그렇지 않으면 false
     */
    private boolean isExpired(Claims claims) {
        if (claims == null || claims.getExpiration() == null) {
            return true;
        }
        return claims.getExpiration().before(new Date());
    }

    /**
     * Access Token에서 Claims를 추출합니다.
     *
     * @param token Access Token
     * @return JWT Claims
     * @throws JwtException 토큰 파싱 실패 시
     */
    private Claims getAccessClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(accessSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JwtException("Failed to parse access token claims", e);
        }
    }

    /**
     * Refresh Token에서 Claims를 추출합니다.
     *
     * @param token Refresh Token
     * @return JWT Claims
     * @throws JwtException 토큰 파싱 실패 시
     */
    private Claims getRefreshClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(refreshSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JwtException("Failed to parse refresh token claims", e);
        }
    }
}