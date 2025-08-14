package com.yapp.backend.filter.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.yapp.backend.common.exception.RedisOperationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token ";
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "access_token_blacklist ";

    @Value("${spring.security.jwt.refresh-token-validity-in-ms}")
    private long refreshTtlMs;

    @Value("${spring.security.jwt.access-token-validity-in-ms}")
    private long accessTtlMs;

    private final StringRedisTemplate redisTemplate;

    private String keyFor(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    private String blacklistKeyFor(String accessToken) {
        return ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken;
    }

    // 로그인 시, 새 리프레시 토큰 저장
    public void storeRefreshOrThrow(Long userId, String refreshToken) {
        try {
            redisTemplate.opsForValue()
                    .set(keyFor(userId), refreshToken, Duration.ofMillis(refreshTtlMs));
            log.debug("Refresh token 저장 완료. userId: {}", userId);
        } catch (Exception e) {
            log.error("Refresh token 저장 실패. userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RedisOperationException("refresh token 저장", userId, e);
        }
    }

    // 재발급 시, 기존 토큰과 비교
    public boolean isValidRefresh(Long userId, String refreshToken) {
        String saved = redisTemplate.opsForValue().get(keyFor(userId));
        return refreshToken.equals(saved);
    }

    // 토큰 회전: old→new 교체
    public void rotateRefresh(Long userId, String newRefreshToken) {
        try {
            redisTemplate.opsForValue()
                    .set(keyFor(userId), newRefreshToken, Duration.ofMillis(refreshTtlMs));
            log.debug("Refresh token 회전 완료. userId: {}", userId);
        } catch (Exception e) {
            log.error("Refresh token 회전 실패. userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RedisOperationException("refresh token 회전", userId, e);
        }
    }

    // 로그아웃 등: 토큰 삭제
    public void deleteRefresh(Long userId) {
        redisTemplate.delete(keyFor(userId));
    }


    // ==================== Access Token 블랙리스트 ====================

    /**
     * Access Token을 블랙리스트에 추가합니다.
     * 로그아웃 시 호출됩니다.
     */
    public void blacklistAccessToken(String accessToken) {
        String blacklistKey = blacklistKeyFor(accessToken);
        // Access Token의 남은 유효시간만큼 블랙리스트에 저장
        redisTemplate.opsForValue()
                .set(blacklistKey, "blacklisted", Duration.ofMillis(accessTtlMs));
    }

    /**
     * Access Token이 블랙리스트에 있는지 확인합니다.
     * JWT 필터에서 호출됩니다.
     */
    public boolean isAccessTokenBlacklisted(String accessToken) {
        String blacklistKey = blacklistKeyFor(accessToken);
        String blacklisted = redisTemplate.opsForValue().get(blacklistKey);
        return blacklisted != null;
    }

    /**
     * 모든 토큰을 삭제합니다 (로그아웃 시).
     */
    public void logoutUser(Long userId, String accessToken) {
        // 1. Refresh Token 삭제
        deleteRefresh(userId);

        // 2. Access Token 블랙리스트 추가
        blacklistAccessToken(accessToken);
    }
}
