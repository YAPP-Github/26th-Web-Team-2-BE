package com.yapp.backend.common.exception;

/**
 * 사용자 권한 검증 실패 시 발생하는 예외
 * 사용자가 특정 리소스(여행보드, 숙소 등)에 접근할 권한이 없을 때 사용
 */
public class UserAuthorizationException extends CustomException {

    public UserAuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserAuthorizationException() {
        super(ErrorCode.USER_AUTHORIZATION_FAILED);
    }

    public UserAuthorizationException(String message) {
        super(ErrorCode.USER_AUTHORIZATION_FAILED, message);
    }

    /**
     * 사용자 ID와 보드 ID를 포함한 상세 메시지로 예외 생성
     */
    public UserAuthorizationException(Long userId, Long tripBoardId) {
        super(ErrorCode.USER_AUTHORIZATION_FAILED,
                String.format("사용자 %d는 보드 %d에 접근 권한이 없습니다", userId, tripBoardId));
    }

    /**
     * 사용자 ID와 숙소 ID를 포함한 상세 메시지로 예외 생성
     */
    public static UserAuthorizationException forAccommodation(Long userId, Long accommodationId) {
        return new UserAuthorizationException(
                String.format("사용자 %d는 숙소 %d에 접근 권한이 없습니다", userId, accommodationId));
    }
}