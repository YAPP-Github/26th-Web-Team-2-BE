package com.yapp.backend.common.exception;

public class TripBoardCreationException extends CustomException {
    public TripBoardCreationException() {
        super(ErrorCode.TRIP_BOARD_CREATION_FAILED);
    }
}