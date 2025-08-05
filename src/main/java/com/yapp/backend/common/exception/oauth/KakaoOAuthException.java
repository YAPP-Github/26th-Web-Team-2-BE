package com.yapp.backend.common.exception.oauth;

import com.yapp.backend.client.dto.KakaoErrorResponse;
import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 카카오 OAuth 관련 예외의 기본 클래스
 */
@Getter
public class KakaoOAuthException extends CustomException {
    
    private final KakaoErrorResponse kakaoError;
    

    public KakaoOAuthException(ErrorCode errorCode, KakaoErrorResponse kakaoError) {
        super(errorCode);
        this.kakaoError = kakaoError;
    }

    public KakaoOAuthException(ErrorCode errorCode, String message, KakaoErrorResponse kakaoError) {
        super(errorCode, message);
        this.kakaoError = kakaoError;
    }

    public String getKakaoErrorCode() {
        return kakaoError != null ? kakaoError.errorCode() : null;
    }
    
    public String getKakaoErrorDescription() {
        return kakaoError != null ? kakaoError.errorDescription() : null;
    }
}