package com.yapp.backend.common.exception;

/**
 * 토큰 생성 과정에서 발생하는 예외
 */
public class TokenGenerationException extends CustomException {
    
    public TokenGenerationException(String message) {
        super(ErrorCode.TOKEN_GENERATION_FAILED, message);
    }
    
    public TokenGenerationException(String message, Throwable cause) {
        super(ErrorCode.TOKEN_GENERATION_FAILED, message, cause);
    }
    
    public TokenGenerationException(Long userId) {
        super(ErrorCode.TOKEN_GENERATION_FAILED, 
              String.format("사용자 %d의 토큰 생성에 실패했습니다.", userId));
    }
    
    public TokenGenerationException(Long userId, Throwable cause) {
        super(ErrorCode.TOKEN_GENERATION_FAILED, 
              String.format("사용자 %d의 토큰 생성에 실패했습니다.", userId), cause);
    }
}
