package com.yapp.backend.common.exception;

/**
 * 잘못된 비교 기준이 제공되었을 때 발생하는 예외
 */
public class InvalidFactorsException extends CustomException {

    public InvalidFactorsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
