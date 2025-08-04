package com.yapp.backend.common.exception;

public class DuplicateInvitationUrlException extends CustomException {
    public DuplicateInvitationUrlException() {
        super(ErrorCode.DUPLICATE_INVITATION_URL);
    }
}