package com.yapp.backend.common.exception.oauth;

import com.yapp.backend.client.dto.KakaoErrorResponse;
import com.yapp.backend.common.exception.ErrorCode;

/**
 * OAuth 요청 파라미터가 잘못된 경우 발생하는 예외
 * 카카오 에러 코드: KOE001
 */
public class OAuthParameterException extends KakaoOAuthException {
    
    public OAuthParameterException(KakaoErrorResponse kakaoError) {
        super(
            ErrorCode.OAUTH_PARAMETER_ERROR,
            "OAuth 요청 파라미터가 올바르지 않습니다.",
            kakaoError
        );
    }
}