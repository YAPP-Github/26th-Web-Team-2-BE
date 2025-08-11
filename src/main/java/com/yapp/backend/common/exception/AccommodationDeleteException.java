package com.yapp.backend.common.exception;

/**
 * 숙소 삭제 중 오류가 발생했을 때 발생하는 예외
 */
public class AccommodationDeleteException extends CustomException {

    public AccommodationDeleteException() {
        super(ErrorCode.ACCOMMODATION_DELETE_FAILED);
    }

    public AccommodationDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }
}