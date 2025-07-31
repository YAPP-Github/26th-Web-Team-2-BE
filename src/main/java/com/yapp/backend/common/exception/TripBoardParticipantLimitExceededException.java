package com.yapp.backend.common.exception;

/**
 * 여행 보드 참여자 수 한계 초과 시 발생하는 예외
 */
public class TripBoardParticipantLimitExceededException extends CustomException {

    public TripBoardParticipantLimitExceededException() {
        super(ErrorCode.TRIP_BOARD_PARTICIPANT_LIMIT_EXCEEDED);
    }
}