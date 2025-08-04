package com.yapp.backend.common.exception;

/**
 * 페이징 파라미터가 유효하지 않을 때 발생하는 예외
 */
public class InvalidPagingParameterException extends CustomException {

    public InvalidPagingParameterException() {
        super(ErrorCode.INVALID_PAGINATION_PARAMETERS);
    }

}