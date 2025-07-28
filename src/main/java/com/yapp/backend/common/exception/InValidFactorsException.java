package com.yapp.backend.common.exception;

public class InValidFactorsException extends CustomException {

    public InValidFactorsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
