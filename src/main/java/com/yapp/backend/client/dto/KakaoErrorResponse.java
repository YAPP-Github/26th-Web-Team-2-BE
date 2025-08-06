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

}