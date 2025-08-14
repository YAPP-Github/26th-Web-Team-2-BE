package com.yapp.backend.common.exception;

/**
 * 유효하지 않은 리프레시 토큰에 대한 예외
 * 토큰 형식이 잘못되었거나 파싱할 수 없는 경우 발생
 */
public class InvalidRefreshTokenException extends CustomException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    public InvalidRefreshTokenException(String message) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, message);
    }

    public InvalidRefreshTokenException(Throwable cause) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, cause);
    }

    public InvalidRefreshTokenException(String message, Throwable cause) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, message, cause);
    }
}
