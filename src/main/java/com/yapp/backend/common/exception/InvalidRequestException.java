package com.yapp.backend.common.exception;

public class InvalidRequestException extends CustomException {

    public InvalidRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
