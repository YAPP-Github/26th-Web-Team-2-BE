package com.yapp.backend.service.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.backend.client.dto.KakaoErrorResponse;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.oauth.InvalidAuthorizationCodeException;
import com.yapp.backend.common.exception.oauth.KakaoOAuthException;
import com.yapp.backend.common.exception.oauth.OAuthClientAuthException;
import com.yapp.backend.common.exception.oauth.OAuthParameterException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * FeignException에서 카카오 OAuth 에러를 파싱하고 적절한 예외로 변환하는 유틸리티
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthErrorParser {
    
    private final ObjectMapper objectMapper;
    
    /**
     * FeignException을 카카오 OAuth 전용 예외로 변환합니다.
     * 
     * @param feignException Feign에서 발생한 예외
     * @param provider OAuth 공급자명 (로그용)
     * @return 변환된 카카오 OAuth 예외
     */
    public KakaoOAuthException parseAndThrow(FeignException feignException, String provider) {
        try {
            // 응답 body에서 에러 정보 추출
            String responseBody = feignException.contentUTF8();
            log.error("{} OAuth 에러 발생 - Status: {}, Body: {}", provider, feignException.status(), responseBody);
            
            // JSON 파싱이 실패할 경우를 대비한 기본 처리
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return new KakaoOAuthException(
                    ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED,
                    null
                );
            }
            
            // 카카오 에러 응답 파싱
            KakaoErrorResponse kakaoError = objectMapper.readValue(responseBody, KakaoErrorResponse.class);
            
            // 에러 코드별로 적절한 예외 생성
            return createSpecificException(kakaoError);
            
        } catch (Exception parseException) {
            // 파싱 실패 시 기본 예외 반환
            return new KakaoOAuthException(
                ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED,
                provider + " OAuth 처리 중 오류가 발생했습니다. (에러코드: " + feignException.status() + ")",
                null
            );
        }
    }
    
    /**
     * 카카오 에러 응답에 따라 구체적인 예외를 생성합니다.
     */
    private KakaoOAuthException createSpecificException(KakaoErrorResponse kakaoError) {
        if (kakaoError.isAuthorizationCodeError()) {
            return new InvalidAuthorizationCodeException(kakaoError);
        }
        
        if (kakaoError.isParameterError()) {
            return new OAuthParameterException(kakaoError);
        }
        
        if (kakaoError.isClientAuthError()) {
            return new OAuthClientAuthException(kakaoError);
        }
        
        // 기타 에러는 일반적인 카카오 OAuth 예외로 처리
        return new KakaoOAuthException(
            ErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED,
            kakaoError
        );
    }
}