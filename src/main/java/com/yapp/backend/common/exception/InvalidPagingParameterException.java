package com.yapp.backend.common.exception;

/**
 * 잘못된 페이징 파라미터가 제공되었을 때 발생하는 예외
 * 페이지 번호가 음수이거나, 페이지 크기가 유효하지 않은 범위일 때 사용됩니다.
 */
public class InvalidPagingParameterException extends CustomException {

    public InvalidPagingParameterException() {
        super(ErrorCode.INVALID_PAGING_PARAMETER);
    }
}