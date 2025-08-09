package com.yapp.backend.common.exception;

/**
 * 비활성화된 초대링크로 요청할 때 발생하는 예외
 */
public class InactiveInvitationUrlException extends CustomException {

    public InactiveInvitationUrlException() {
        super(ErrorCode.INACTIVE_INVITATION_URL);
    }
}