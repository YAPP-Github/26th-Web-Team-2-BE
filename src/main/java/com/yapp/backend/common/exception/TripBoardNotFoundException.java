package com.yapp.backend.common.exception;

/**
 * 여행보드를 찾을 수 없을 때 발생하는 예외
 */
public class TripBoardNotFoundException extends CustomException {

    public TripBoardNotFoundException() {
        super(ErrorCode.TRIP_BOARD_NOT_FOUND);
    }
}