package com.yapp.backend.common.exception.oauth;

import com.yapp.backend.client.dto.KakaoErrorResponse;
import com.yapp.backend.common.exception.ErrorCode;

/**
 * OAuth 클라이언트 인증이 실패한 경우 발생하는 예외
 * 카카오 에러 코드: KOE004
 */
public class OAuthClientAuthException extends KakaoOAuthException {
    
    public OAuthClientAuthException(KakaoErrorResponse kakaoError) {
        super(
            ErrorCode.OAUTH_CLIENT_AUTH_ERROR,
            "OAuth 클라이언트 인증에 실패했습니다. 클라이언트 정보를 확인해 주세요.",
            kakaoError
        );
    }
}