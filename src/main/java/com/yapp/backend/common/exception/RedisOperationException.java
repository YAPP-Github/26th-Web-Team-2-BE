package com.yapp.backend.common.exception;

/**
 * Redis 연산 실패 시 발생하는 예외
 */
public class RedisOperationException extends CustomException {
    
    public RedisOperationException(String message) {
        super(ErrorCode.REDIS_OPERATION_FAILED, message);
    }
    
    public RedisOperationException(String message, Throwable cause) {
        super(ErrorCode.REDIS_OPERATION_FAILED, message, cause);
    }
    
    public RedisOperationException(String operation, Long userId) {
        super(ErrorCode.REDIS_OPERATION_FAILED, 
              String.format("Redis %s 작업이 실패했습니다. userId: %d", operation, userId));
    }
    
    public RedisOperationException(String operation, Long userId, Throwable cause) {
        super(ErrorCode.REDIS_OPERATION_FAILED, 
              String.format("Redis %s 작업이 실패했습니다. userId: %d", operation, userId), cause);
    }
}
