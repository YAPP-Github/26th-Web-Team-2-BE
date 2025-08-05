package com.yapp.backend.service.oauth;

import com.yapp.backend.filter.dto.SocialUserInfo;

/**
 * Authorization Code Flow 방식의 OAuth Provider 어댑터 인터페이스
 * 각 OAuth 공급자(카카오, 구글, 네이버 등)별로 구현하여 사용자 정보 추출을 담당합니다.
 */
public interface OAuthCodeUserInfoProvider {
    
    /**
     * OAuth 공급자 식별키를 반환합니다.
     * 
     * @return 공급자 식별키 (예: "kakao", "google", "naver")
     */
    String getProviderKey();
    
    /**
     * 인가 코드를 통해 액세스 토큰을 획득하고 사용자 정보를 조회하여 
     * 공통 SocialUserInfo 객체로 변환합니다.
     * 
     * @param code OAuth 인가 코드
     * @param baseUrl 클라이언트의 베이스 URL (토큰 교환 시 redirect_uri 생성용)
     * @return 공통 사용자 정보 객체
     */
    SocialUserInfo getUserInfoByCode(String code, String baseUrl);
    
    /**
     * OAuth 인가 URL을 생성합니다.
     * 
     * @param baseUrl 클라이언트의 베이스 URL (예: http://localhost:3000)
     * @return OAuth 인가 URL
     */
    String generateAuthorizeUrl(String baseUrl);
}