package com.yapp.backend.common.exception;

/**
 * 만료된 리프레시 토큰에 대한 예외
 * Redis에 저장된 토큰과 일치하지 않거나 만료된 경우 발생
 */
public class ExpiredRefreshTokenException extends CustomException {

    public ExpiredRefreshTokenException() {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }

    public ExpiredRefreshTokenException(String message) {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN, message);
    }

    public ExpiredRefreshTokenException(Throwable cause) {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN, cause);
    }

    public ExpiredRefreshTokenException(String message, Throwable cause) {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN, message, cause);
    }

    /**
     * 사용자 ID를 포함한 상세 메시지로 예외 생성
     * 
     * @param userId 사용자 ID
     */
    public ExpiredRefreshTokenException(Long userId) {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN, 
              String.format("사용자 %d의 리프레시 토큰이 만료되었거나 유효하지 않습니다", userId));
    }
}
