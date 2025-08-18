package com.yapp.backend.common.exception;

public class InvalidComparisonTable extends CustomException {

    public InvalidComparisonTable(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidComparisonTable(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
