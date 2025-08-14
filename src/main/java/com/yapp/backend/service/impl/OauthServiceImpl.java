package com.yapp.backend.service.impl;

import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.config.OAuthSecurityProperties;
import com.yapp.backend.controller.dto.response.OauthLoginResponse;
import com.yapp.backend.controller.dto.response.TokenSuccessResponse;
import com.yapp.backend.filter.dto.SocialUserInfo;
import com.yapp.backend.filter.service.RefreshTokenService;
import com.yapp.backend.service.OauthService;
import com.yapp.backend.service.UserLoginService;
import com.yapp.backend.service.model.User;
import com.yapp.backend.service.oauth.OAuthCodeUserInfoProvider;
import com.yapp.backend.common.exception.oauth.InvalidBaseUrlException;
import com.yapp.backend.common.exception.oauth.UnsupportedOAuthProviderException;
import com.yapp.backend.common.exception.InvalidRefreshTokenException;
import com.yapp.backend.common.exception.ExpiredRefreshTokenException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OauthServiceImpl implements OauthService {
    
    private final UserLoginService userLoginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final OAuthSecurityProperties oauthSecurityProperties;
    private final Map<String, OAuthCodeUserInfoProvider> providers;
    
    public OauthServiceImpl(
            List<OAuthCodeUserInfoProvider> providerList,
            UserLoginService userLoginService,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService,
            OAuthSecurityProperties oauthSecurityProperties
    ) {
        // Spring이 빈 이름 기준으로 자동 주입
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuthCodeUserInfoProvider::getProviderKey, p -> p));
        this.userLoginService = userLoginService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.oauthSecurityProperties = oauthSecurityProperties;
    }
    
    @Override
    public String generateAuthorizeUrl(String provider, String baseUrl) {
        validateBaseUrl(baseUrl);
        OAuthCodeUserInfoProvider userInfoProvider = getProviderOrThrow(provider);
        return userInfoProvider.generateAuthorizeUrl(baseUrl);
    }
    
    @Override
    public OauthLoginResponse exchangeCodeForToken(String provider, String code, String baseUrl) {
        validateBaseUrl(baseUrl);

        OAuthCodeUserInfoProvider userInfoProvider = getProviderOrThrow(provider);

        // 1. 공급자별 어댑터를 통해 사용자 정보 조회
        SocialUserInfo socialUserInfo = userInfoProvider.getUserInfoByCode(code, baseUrl);

        // 2. SocialUserInfo를 User 객체로 변환
        User socialUser = socialUserInfo.toModel();

        // 3. 사용자 저장 또는 조회
        User user = userLoginService.handleOAuthLogin(socialUser);

        // 4. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 5. Redis에 Refresh Token 저장
        refreshTokenService.storeRefresh(user.getId(), refreshToken);

        // 6. 토큰 정보를 포함한 응답 생성
        OauthLoginResponse response = new OauthLoginResponse(
                user.getId(),
                socialUserInfo.getNickname()
        );
        response.deliverToken(accessToken, refreshToken);
        
        log.info("OAuth 로그인 완료. userId: {}, provider: {}", user.getId(), provider);
        return response;
    }

    private OAuthCodeUserInfoProvider getProviderOrThrow(String provider) {
        OAuthCodeUserInfoProvider userInfoProvider = providers.get(provider);
        if (userInfoProvider == null) {
            log.warn("Unsupported OAuth provider requested: {}", provider);
            throw new UnsupportedOAuthProviderException(provider);
        }
        return userInfoProvider;
    }

    /**
     * Base URL 유효성 검증 및 허용된 도메인 화이트리스트 체크
     *
     * @param baseUrl 검증할 base URL
     * @throws InvalidBaseUrlException 허용되지 않은 base URL인 경우
     */
    private void validateBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new InvalidBaseUrlException("Base URL이 제공되지 않았습니다.");
        }
        
        // URL 형식 기본 검증 (http:// 또는 https://로 시작하는지)
        String trimmedUrl = baseUrl.trim();
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            throw new InvalidBaseUrlException("올바르지 않은 URL 형식입니다: " + trimmedUrl);
        }
        
        // 허용된 도메인 화이트리스트 검증
        List<String> allowedDomains = oauthSecurityProperties.getAllowedDomains();
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            throw new InvalidBaseUrlException("허용된 도메인 설정이 없습니다.");
        }
        
        boolean isAllowed = allowedDomains.stream()
                .anyMatch(allowedDomain -> {
                    // 정확한 매칭 또는 하위 경로 허용 (쿼리 파라미터 제외)
                    String normalizedBaseUrl = normalizeUrl(trimmedUrl);
                    String normalizedAllowedDomain = normalizeUrl(allowedDomain);
                    return normalizedBaseUrl.equals(normalizedAllowedDomain) || 
                           normalizedBaseUrl.startsWith(normalizedAllowedDomain + "/");
                });
        
        if (!isAllowed)
            throw new InvalidBaseUrlException("허용되지 않은 도메인입니다: " + trimmedUrl);
    }
    
    /**
     * URL 정규화 (끝의 슬래시 제거, 쿼리 파라미터 제거)
     */
    private String normalizeUrl(String url) {
        if (url == null) return "";
        
        // 쿼리 파라미터와 프래그먼트 제거
        String normalized = url.split("\\?")[0].split("#")[0];
        
        // 끝의 슬래시 제거 (루트 URL이 아닌 경우)
        if (normalized.endsWith("/") && normalized.length() > 1) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        
        return normalized;
    }

    @Override
    public TokenSuccessResponse refreshTokens(String refreshToken) {
        log.info("리프레시 토큰 재발급 요청");
        
        // 1. 리프레시 토큰 유효성 검증
        Claims refreshClaims = jwtTokenProvider.parseAndValidateRefreshToken(refreshToken);
        if (refreshClaims == null) {
            log.warn("유효하지 않은 리프레시 토큰입니다");
            throw new InvalidRefreshTokenException("리프레시 토큰이 유효하지 않거나 파싱할 수 없습니다");
        }

        // 2. 리프레시 토큰에서 사용자 ID 추출
        Long userId = Long.valueOf(jwtTokenProvider.getRefreshUsername(refreshToken));

        // 3. Redis에 저장된 토큰과 일치하는지 검증
        if (!refreshTokenService.isValidRefresh(userId, refreshToken)) {
            log.warn("Redis에 저장된 토큰과 일치하지 않음. userId: {}", userId);
            throw new ExpiredRefreshTokenException(userId);
        }

        // 4. 새로운 액세스 토큰과 리프레시 토큰 생성 (JwtTokenProvider에서 예외 처리됨)
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
        
        log.debug("리프레시 토큰 재발급을 위한 새 토큰 생성 완료. userId: {}", userId);

        // 5. Redis에서 리프레시 토큰 회전 (기존 토큰 무효화 및 새 토큰 저장)
        refreshTokenService.rotateRefresh(userId, newRefreshToken);

        log.info("리프레시 토큰 재발급 완료. userId: {}", userId);
        return new TokenSuccessResponse(newAccessToken, newRefreshToken);
    }
}