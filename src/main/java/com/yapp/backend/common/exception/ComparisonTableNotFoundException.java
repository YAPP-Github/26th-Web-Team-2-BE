package com.yapp.backend.common.exception;

public class ComparisonTableNotFoundException extends CustomException {

    public ComparisonTableNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
