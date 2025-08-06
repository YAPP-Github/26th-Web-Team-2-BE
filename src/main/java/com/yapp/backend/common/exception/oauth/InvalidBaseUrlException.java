package com.yapp.backend.common.exception.oauth;

import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;

/**
 * 허용되지 않은 Base URL로 OAuth 요청 시 발생하는 예외
 */
public class InvalidBaseUrlException extends CustomException {
    
    public InvalidBaseUrlException(String baseUrl) {
        super(ErrorCode.INVALID_OAUTH_BASE_URL, 
              String.format("허용되지 않은 Base URL입니다: %s", baseUrl));
    }
}