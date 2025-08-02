package com.yapp.backend.common.exception;

public class TripBoardParticipantLimitExceededException extends CustomException {
    public TripBoardParticipantLimitExceededException() {
        super(ErrorCode.TRIP_BOARD_PARTICIPANT_LIMIT_EXCEEDED);
    }
}