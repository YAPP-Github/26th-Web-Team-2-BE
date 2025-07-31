package com.yapp.backend.common.exception;

public class AccommodationNotFoundException extends CustomException {

    public AccommodationNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
