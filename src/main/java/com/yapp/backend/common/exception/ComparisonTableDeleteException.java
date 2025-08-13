package com.yapp.backend.common.exception;

/**
 * 비교표 삭제 중 오류가 발생했을 때 발생하는 예외
 */
public class ComparisonTableDeleteException extends CustomException {

    public ComparisonTableDeleteException() {
        super(ErrorCode.COMPARISON_TABLE_DELETE_FAILED);
    }

    public ComparisonTableDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }
}