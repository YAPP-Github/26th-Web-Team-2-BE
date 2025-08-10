package com.yapp.backend.common.exception;

public class DeletedUserException extends CustomException {

    public DeletedUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
