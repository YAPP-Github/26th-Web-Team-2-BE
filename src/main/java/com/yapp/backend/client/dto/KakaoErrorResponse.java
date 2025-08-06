package com.yapp.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yapp.backend.common.exception.oauth.KakaoErrorCode;
import lombok.Getter;

/**
 * 카카오 OAuth API 에러 응답 DTO
 */
@Getter
public class KakaoErrorResponse {

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("error_code")
    private String errorCode;

    /**
     * 인가 코드 관련 에러인지 확인
     */
    public boolean isAuthorizationCodeError() {
        KakaoErrorCode errorCodeEnum = KakaoErrorCode.fromCode(errorCode);
        KakaoErrorCode errorEnum = KakaoErrorCode.fromCode(error);
        
        return (errorCodeEnum != null && errorCodeEnum.isAuthorizationCodeError()) ||
               (errorEnum != null && errorEnum.isAuthorizationCodeError());
    }
    
    /**
     * 파라미터 오류인지 확인
     */
    public boolean isParameterError() {
        KakaoErrorCode errorCodeEnum = KakaoErrorCode.fromCode(errorCode);
        KakaoErrorCode errorEnum = KakaoErrorCode.fromCode(error);
        
        return (errorCodeEnum != null && errorCodeEnum.isParameterError()) ||
               (errorEnum != null && errorEnum.isParameterError());
    }
    
    /**
     * 클라이언트 인증 오류인지 확인
     */
    public boolean isClientAuthError() {
        KakaoErrorCode errorCodeEnum = KakaoErrorCode.fromCode(errorCode);
        KakaoErrorCode errorEnum = KakaoErrorCode.fromCode(error);
        
        return (errorCodeEnum != null && errorCodeEnum.isClientAuthError()) ||
               (errorEnum != null && errorEnum.isClientAuthError());
    }
    
    /**
     * 에러 코드에 해당하는 KakaoErrorCode 반환
     */
    public KakaoErrorCode getKakaoErrorCode() {
        KakaoErrorCode errorCodeEnum = KakaoErrorCode.fromCode(errorCode);
        if (errorCodeEnum != null) {
            return errorCodeEnum;
        }
        return KakaoErrorCode.fromCode(error);
    }
}