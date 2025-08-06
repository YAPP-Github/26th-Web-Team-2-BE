package com.yapp.backend.common.exception.oauth;

import org.springframework.http.HttpStatus;
import lombok.Getter;

/**
 * 카카오 OAuth API 에러 코드 관리 enum
 */
@Getter
public enum KakaoErrorCode {
    
    // 카카오 내부 에러 코드
    KOE001("KOE001", "필수 파라미터가 누락되었거나 잘못된 경우", HttpStatus.BAD_REQUEST),
    KOE004("KOE004",  "클라이언트 인증에 실패한 경우", HttpStatus.UNAUTHORIZED),
    KOE320("KOE320", "인가 코드가 유효하지 않은 경우", HttpStatus.BAD_REQUEST),
    
    // OAuth 2.0 표준 에러 코드
    INVALID_REQUEST("invalid_request", "요청이 잘못되었거나 필수 파라미터가 누락된 경우", HttpStatus.BAD_REQUEST),
    INVALID_CLIENT("invalid_client", "클라이언트 인증에 실패한 경우", HttpStatus.UNAUTHORIZED),
    INVALID_GRANT("invalid_grant", "인가 코드가 유효하지 않은 경우", HttpStatus.BAD_REQUEST)
    ;
    
    private final String title;
    private final String message;
    private final HttpStatus httpStatus;
    
    KakaoErrorCode(String title, String message, HttpStatus httpStatus) {
        this.title = title;
        this.message = message;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 에러 코드로 KakaoErrorCode 찾기
     */
    public static KakaoErrorCode fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (KakaoErrorCode errorCode : values()) {
            if (errorCode.getTitle().equals(code)) {
                return errorCode;
            }
        }
        return null;
    }
    
    
    /**
     * 에러 타입별 분류 메서드들
     */
    public boolean isParameterError() {
        return this == KOE001 || this == INVALID_REQUEST;
    }
    
    public boolean isClientAuthError() {
        return this == KOE004 || this == INVALID_CLIENT;
    }
    
    public boolean isAuthorizationCodeError() {
        return this == KOE320 || this == INVALID_GRANT;
    }
}
