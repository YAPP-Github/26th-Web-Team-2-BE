package com.yapp.backend.common.exception;

public class InvalidTravelPeriodException extends CustomException {
    public InvalidTravelPeriodException() {
        super(ErrorCode.INVALID_TRAVEL_PERIOD);
    }
}