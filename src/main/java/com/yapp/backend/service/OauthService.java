package com.yapp.backend.service;

import com.yapp.backend.controller.dto.response.OauthLoginResponse;

public interface OauthService {
    
    /**
     * 지정된 OAuth 공급자의 인가 URL을 생성합니다.
     * 
     * @param provider OAuth 공급자 (kakao, google, naver 등)
     * @return OAuth 인가 URL
     */
    String generateAuthorizeUrl(String provider);
    
    /**
     * 지정된 OAuth 공급자의 인가 코드를 통해 토큰을 교환하고 사용자 정보를 조회하여 JWT를 발급합니다.
     * 
     * @param provider OAuth 공급자 (kakao, google, naver 등)
     * @param code OAuth 공급자에서 발급받은 인가 코드
     * @return JWT 토큰 응답
     */
    OauthLoginResponse exchangeCodeForToken(String provider, String code);

    /**
     * 토큰 정보를 담는 레코드
     */
    record TokenInfo(String accessToken, String refreshToken) {}
}