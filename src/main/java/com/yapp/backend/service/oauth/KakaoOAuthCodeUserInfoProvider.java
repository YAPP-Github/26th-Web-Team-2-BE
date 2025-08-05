package com.yapp.backend.service.oauth;

import com.yapp.backend.client.KakaoApiClient;
import com.yapp.backend.client.KakaoOauthClient;
import com.yapp.backend.client.dto.KakaoTokenResponse;
import com.yapp.backend.client.dto.KakaoUserInfoResponse;
import com.yapp.backend.filter.dto.SocialUserInfo;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Component("kakaoCodeProvider")
@RequiredArgsConstructor
public class KakaoOAuthCodeUserInfoProvider implements OAuthCodeUserInfoProvider {
    
    private final KakaoOauthClient kakaoOauthClient;
    private final KakaoApiClient kakaoApiClient;
    private final KakaoOAuthErrorParser errorParser;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    
    // OAuth Properties에서 스코프를 가져오도록 변경
    
    private static final String KAKAO_AUTHORIZE_URL = "https://kauth.kakao.com/oauth/authorize";
    
    @Override
    public String getProviderKey() {
        return "kakao";
    }
    
    @Override
    public String generateAuthorizeUrl() {
        return UriComponentsBuilder.fromHttpUrl(KAKAO_AUTHORIZE_URL)
                .queryParam("client_id", kakaoClientId)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "profile_nickname,profile_image")
                .build()
                .toUriString();
    }
    
    @Override
    public SocialUserInfo getUserInfoByCode(String code) {
        try {
            // 1. 카카오에서 액세스 토큰 획득
            KakaoTokenResponse kakaoTokenResponse = kakaoOauthClient.getAccessToken(
                    "authorization_code",
                    kakaoClientId,
                    kakaoClientSecret,
                    kakaoRedirectUri,
                    code
            );

            // 2. 액세스 토큰으로 사용자 정보 조회
            String authorization = "Bearer " + kakaoTokenResponse.accessToken();
            KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(authorization);

            // 3. 공통 SocialUserInfo 객체로 변환
            return new SocialUserInfo(
                    getProviderKey(),
                    userInfo.id().toString(),
                    userInfo.getEmail(),
                    userInfo.getNickname(),
                    userInfo.getProfileImageUrl()
            );
            
        } catch (FeignException feignException) {
            throw errorParser.parseAndThrow(feignException, getProviderKey());
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 조회에 실패했습니다.", e);
        }
    }
}