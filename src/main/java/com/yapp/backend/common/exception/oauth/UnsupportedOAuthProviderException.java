package com.yapp.backend.common.exception.oauth;

import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;

/**
 * 지원하지 않는 OAuth 공급자에 대한 요청 시 발생하는 예외
 */
public class UnsupportedOAuthProviderException extends CustomException {
    
    public UnsupportedOAuthProviderException(String provider) {
        super(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER, 
              String.format("지원하지 않는 OAuth 공급자입니다: %s", provider));
    }
}