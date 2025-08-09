package com.yapp.backend.common.exception;

/**
 * 유효하지 않은 초대링크로 요청할 때 발생하는 예외
 */
public class InvalidInvitationUrlException extends CustomException {

    public InvalidInvitationUrlException() {
        super(ErrorCode.INVALID_INVITATION_URL);
    }
}