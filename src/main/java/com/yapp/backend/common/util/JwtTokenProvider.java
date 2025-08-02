package com.yapp.backend.common.util;

import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.filter.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${spring.security.jwt.access-token-validity-in-ms}")
    private long accessTokenValidityInMs;           // 1 hour
    private final SecretKey accessSecretKey;

    @Value("${spring.security.jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidityInMs;           // 7 days
    private final SecretKey refreshSecretKey;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.security.cookie.domain}")
    private String cookieDomain;

    @Value("${app.security.cookie.secure}")
    private boolean cookieSecure;

    public JwtTokenProvider(
            @Value("${spring.security.jwt.access-secret-key}") String accessKey,
            @Value("${spring.security.jwt.refresh-secret-key}") String refreshKey,
            RefreshTokenService refreshTokenService
    ) {
        this.accessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey.replaceAll("\\s+", "")));
        this.refreshSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey.replaceAll("\\s+", "")));
        this.refreshTokenService = refreshTokenService;
    }

    public ResponseCookie generateAccessTokenCookie(Long userId) {
        long maxAgeInSeconds = accessTokenValidityInMs / 1_000;
        String accessToken = createAccessToken(userId);
        return ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .domain(cookieDomain)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAgeInSeconds)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie generateRefreshTokenCookie(Long userId) {
        long maxAgeInSeconds = refreshTokenValidityInMs / 1_000;
        String refreshToken = createRefreshToken(userId);
        refreshTokenService.storeRefresh(userId, refreshToken);
        return ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .domain(cookieDomain)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAgeInSeconds)
                .sameSite("Lax")
                .build();
    }
    // subject를 userId로 세팅
    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValidityInMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))                 // 사용자 식별 정보
                .setIssuedAt(now)                                   // 발행 시간
                .setExpiration(exp)                                 // 만료 시간
                .signWith(this.accessSecretKey, SignatureAlgorithm.HS256) // 서명 알고리즘
                .compact();
    }
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenValidityInMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))                 // 사용자 식별 정보
                .setIssuedAt(now)                                   // 발행 시간
                .setExpiration(exp)                                 // 만료 시간
                .signWith(this.refreshSecretKey, SignatureAlgorithm.HS256) // 서명 알고리즘
                .compact();
    }

    public UserDetails createUserDetails(String token, String socialId) {
        return new CustomUserDetails(
                Long.valueOf(getUserIdFromToken(token)),
                socialId,
                List.of()
        );
    }

    // 토큰이 유효한지 검증
    public boolean validateAccessTokenOrThrow(String token) {
//        if (token == null) {
        if (token == null || getUserIdFromToken(token) == null) {
            return false;
        }

        try {
            Claims claims = getAccessClaims(token);
            if (claims.getSubject() == null) {
                return false;
            }
            return !isExpired(claims);
        } catch (ExpiredJwtException expiredJwtException) {
            throw expiredJwtException;
        } catch (JwtException | IllegalArgumentException e) {
            // any other parsing/signature/format error
            throw new JwtException("Invalid access token", e);
        }
    }
    public Claims parseAndValidateRefreshToken(String token) {
        if (token == null) {
            return null;
        }
        try {
            Claims refreshClaims = getRefreshClaims(token);
            if(!isExpired(refreshClaims))
                return refreshClaims;
            return null;
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            throw e;
        } catch (JwtException e) {
            // 서명 오류 / 토큰 변조 / 포맷 오류 등
            throw e;
        }
    }

    // token 내에서 userId(subject)를 꺼내오는 메소드
    public String getUserIdFromToken(String token) {
        return getAccessClaims(token).getSubject();
    }

    public String getRefreshUsername(String token) {
        return getRefreshClaims(token).getSubject();
    }



    // 토큰의 만료 여부를 확인
    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());   // 만료 시간이 현재보다 전에 있으면 Expired true
    }


    private Claims getAccessClaims(String token) {
        return Jwts.parser()
                .verifyWith(accessSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims getRefreshClaims(String token) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}