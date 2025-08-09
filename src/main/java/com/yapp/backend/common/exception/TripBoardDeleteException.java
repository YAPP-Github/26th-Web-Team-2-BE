package com.yapp.backend.common.exception;

/**
 * 여행보드 삭제 중 오류가 발생했을 때 발생하는 예외
 */
public class TripBoardDeleteException extends CustomException {

    public TripBoardDeleteException() {
        super(ErrorCode.TRIP_BOARD_DELETE_FAILED);
    }
}