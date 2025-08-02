package com.yapp.backend.common.exception;

public class UserAuthorizationException extends CustomException {

    public UserAuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
