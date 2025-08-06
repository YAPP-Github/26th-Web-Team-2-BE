package com.yapp.backend.common.exception.oauth;

import com.yapp.backend.client.dto.KakaoErrorResponse;
import com.yapp.backend.common.exception.ErrorCode;

/**
 * 인가 코드가 유효하지 않거나 이미 사용된 경우 발생하는 예외
 * 카카오 에러 코드: KOE320
 */
public class InvalidAuthorizationCodeException extends KakaoOAuthException {
    
    public InvalidAuthorizationCodeException(KakaoErrorResponse kakaoError) {
        super(
            ErrorCode.INVALID_AUTHORIZATION_CODE,
            "인가 코드가 유효하지 않거나 이미 사용되었습니다. 새로운 인가 코드를 발급받아 주세요.",
            kakaoError
        );
    }
}