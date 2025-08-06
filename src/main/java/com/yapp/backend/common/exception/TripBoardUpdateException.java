package com.yapp.backend.common.exception;

/**
 * 여행보드 수정 중 오류가 발생했을 때 발생하는 예외
 */
public class TripBoardUpdateException extends CustomException {

    public TripBoardUpdateException() {
        super(ErrorCode.TRIP_BOARD_UPDATE_FAILED);
    }
}