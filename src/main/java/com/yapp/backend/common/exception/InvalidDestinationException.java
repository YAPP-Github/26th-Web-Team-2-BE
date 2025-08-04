package com.yapp.backend.common.exception;

public class InvalidDestinationException extends CustomException {
    public InvalidDestinationException() {
        super(ErrorCode.INVALID_DESTINATION);
    }
}