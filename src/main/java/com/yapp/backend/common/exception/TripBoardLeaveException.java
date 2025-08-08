package com.yapp.backend.common.exception;

/**
 * 여행보드 나가기 관련 예외 클래스
 */
public class TripBoardLeaveException extends CustomException {

    public TripBoardLeaveException() {
        super(ErrorCode.TRIP_BOARD_LEAVE_FAILED);
    }

    public TripBoardLeaveException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TripBoardLeaveException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}