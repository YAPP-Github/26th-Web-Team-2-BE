package com.yapp.backend.common.exception;

/**
 * 여행 보드 생성 시 발생하는 예외
 */
public class TripBoardCreationException extends CustomException {
    public TripBoardCreationException(String message) {
        super(ErrorCode.TRIP_BOARD_CREATION_FAILED);
    }
}