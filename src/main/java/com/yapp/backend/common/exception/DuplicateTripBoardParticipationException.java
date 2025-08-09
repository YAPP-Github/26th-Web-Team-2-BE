package com.yapp.backend.common.exception;

/**
 * 이미 참여한 여행 보드에 중복 참여를 시도할 때 발생하는 예외
 */
public class DuplicateTripBoardParticipationException extends CustomException {

    public DuplicateTripBoardParticipationException() {
        super(ErrorCode.DUPLICATE_TRIP_BOARD_PARTICIPATION);
    }
}