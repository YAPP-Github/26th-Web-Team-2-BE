package com.yapp.backend.common.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCode {
    CUSTOM_ERROR("커스텀 에러", "이것은 커스텀 예외입니다.", HttpStatus.BAD_REQUEST),

    /**
     * 4xx
     */
    USER_NOT_FOUND("사용자 존재하지 않음", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND)
    // 필요에 따라 추가
    ;

    private final String title;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String title, String message, HttpStatus httpStatus) {
        this.title = title;
        this.message = message;
        this.httpStatus = httpStatus;
    }
} 