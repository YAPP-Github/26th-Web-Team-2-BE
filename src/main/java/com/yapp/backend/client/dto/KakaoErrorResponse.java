package com.yapp.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 OAuth API 에러 응답 DTO
 */
public record KakaoErrorResponse(
        String error,
        
        @JsonProperty("error_description")
        String errorDescription,
        
        @JsonProperty("error_code")
        String errorCode
) {
    /**
     * 인가 코드 관련 에러인지 확인
     */
    public boolean isAuthorizationCodeError() {
        return "KOE320".equals(errorCode) || "invalid_grant".equals(error);
    }
    
    /**
     * 파라미터 오류인지 확인
     */
    public boolean isParameterError() {
        return "KOE001".equals(errorCode) || "invalid_request".equals(error);
    }
    
    /**
     * 클라이언트 인증 오류인지 확인
     */
    public boolean isClientAuthError() {
        return "KOE004".equals(errorCode) || "invalid_client".equals(error);
    }
}