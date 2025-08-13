package com.yapp.backend.service;

import com.yapp.backend.common.exception.ExpiredRefreshTokenException;
import com.yapp.backend.common.exception.InvalidRefreshTokenException;
import com.yapp.backend.controller.dto.response.OauthLoginResponse;
import com.yapp.backend.controller.dto.response.TokenSuccessResponse;

public interface OauthService {
    
    /**
     * 지정된 OAuth 공급자의 인가 URL을 생성합니다.
     * 
     * @param provider OAuth 공급자 (kakao, google, naver 등)
     * @param baseUrl 클라이언트의 베이스 URL (예: http://localhost:3000)
     * @return OAuth 인가 URL
     */
    String generateAuthorizeUrl(String provider, String baseUrl);
    
    /**
     * 지정된 OAuth 공급자의 인가 코드를 통해 토큰을 교환하고 사용자 정보를 조회하여 JWT를 발급합니다.
     * 토큰 생성, Redis 저장, 사용자 정보 조회를 일괄 처리합니다.
     * 
     * @param provider OAuth 공급자 (kakao, google, naver 등)
     * @param code OAuth 공급자에서 발급받은 인가 코드
     * @param baseUrl 클라이언트의 베이스 URL (토큰 교환 시 redirect_uri 생성용)
     * @return 사용자 정보와 토큰 정보를 포함한 응답
     */
    OauthLoginResponse exchangeCodeForToken(String provider, String code, String baseUrl);

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     * 기존 리프레시 토큰은 무효화되고 새로운 토큰 쌍이 생성됩니다.
     * 토큰 회전(Token Rotation) 정책을 통해 보안을 강화합니다.
     * 
     * @param refreshToken 유효한 리프레시 토큰
     * @return 새로 발급된 액세스 토큰과 리프레시 토큰
     * @throws InvalidRefreshTokenException 리프레시 토큰이 유효하지 않거나 파싱할 수 없는 경우
     * @throws ExpiredRefreshTokenException 리프레시 토큰이 만료되었거나 Redis에 저장된 토큰과 일치하지 않는 경우
     */
    TokenSuccessResponse refreshTokens(String refreshToken);
}