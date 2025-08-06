package com.yapp.backend.service.impl;

import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.config.OAuthSecurityProperties;
import com.yapp.backend.controller.dto.response.OauthLoginResponse;
import com.yapp.backend.filter.dto.SocialUserInfo;
import com.yapp.backend.service.OauthService;
import com.yapp.backend.service.UserLoginService;
import com.yapp.backend.service.model.User;
import com.yapp.backend.service.oauth.OAuthCodeUserInfoProvider;
import com.yapp.backend.common.exception.oauth.InvalidBaseUrlException;
import com.yapp.backend.common.exception.oauth.UnsupportedOAuthProviderException;
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
    private final OAuthSecurityProperties oauthSecurityProperties;
    private final Map<String, OAuthCodeUserInfoProvider> providers;
    
    public OauthServiceImpl(
            List<OAuthCodeUserInfoProvider> providerList,
            UserLoginService userLoginService,
            JwtTokenProvider jwtTokenProvider,
            OAuthSecurityProperties oauthSecurityProperties
    ) {
        // Spring이 빈 이름 기준으로 자동 주입
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuthCodeUserInfoProvider::getProviderKey, p -> p));
        this.userLoginService = userLoginService;
        this.jwtTokenProvider = jwtTokenProvider;
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

        // 4. 사용자 정보로 응답 생성 (토큰은 컨트롤러에서 쿠키로 설정)
        return new OauthLoginResponse(
                user.getId(),
                socialUserInfo.getNickname()
        );
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
}