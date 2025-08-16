package com.yapp.backend.common.exception;

/**
 * 공유 코드 관련 예외
 */
public class ShareCodeException extends CustomException {
    
    public ShareCodeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
