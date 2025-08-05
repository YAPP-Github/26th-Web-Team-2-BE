package com.yapp.backend.service.impl;

import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.controller.dto.response.OauthTokenResponse;
import com.yapp.backend.filter.dto.SocialUserInfo;
import com.yapp.backend.service.OauthService;
import com.yapp.backend.service.UserLoginService;
import com.yapp.backend.service.model.User;
import com.yapp.backend.service.oauth.OAuthCodeUserInfoProvider;
import com.yapp.backend.common.exception.oauth.KakaoOAuthException;
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
    private final Map<String, OAuthCodeUserInfoProvider> providers;
    
    public OauthServiceImpl(
            List<OAuthCodeUserInfoProvider> providerList,
            UserLoginService userLoginService,
            JwtTokenProvider jwtTokenProvider
    ) {
        // Spring이 빈 이름 기준으로 자동 주입
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuthCodeUserInfoProvider::getProviderKey, p -> p));
        this.userLoginService = userLoginService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public String generateAuthorizeUrl(String provider) {
        OAuthCodeUserInfoProvider userInfoProvider = getProviderOrThrow(provider);
        return userInfoProvider.generateAuthorizeUrl();
    }
    
    @Override
    public OauthTokenResponse exchangeCodeForToken(String provider, String code) {
        try {
            OAuthCodeUserInfoProvider userInfoProvider = getProviderOrThrow(provider);
            
            // 1. 공급자별 어댑터를 통해 사용자 정보 조회
            SocialUserInfo socialUserInfo = userInfoProvider.getUserInfoByCode(code);

            // 2. SocialUserInfo를 User 객체로 변환
            User socialUser = socialUserInfo.toModel();
            
            // 3. 사용자 저장 또는 조회
            User user = userLoginService.handleOAuthLogin(socialUser);
            
            // 4. JWT 토큰 발급
            String accessToken = jwtTokenProvider.createAccessToken(user.getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
            
            return new OauthTokenResponse(user.getId(), accessToken, refreshToken);
            
        } catch (KakaoOAuthException e) {
            log.warn("{} OAuth 처리 중 카카오 OAuth 예외 발생: {}", provider, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{} OAuth 토큰 교환 중 예상치 못한 오류 발생", provider, e);
            throw new RuntimeException(provider + " OAuth 토큰 교환 중 예상치 못한 오류가 발생했습니다.", e);
        }
    }
    
    private OAuthCodeUserInfoProvider getProviderOrThrow(String provider) {
        OAuthCodeUserInfoProvider userInfoProvider = providers.get(provider);
        if (userInfoProvider == null) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 공급자입니다: " + provider);
        }
        return userInfoProvider;
    }
}