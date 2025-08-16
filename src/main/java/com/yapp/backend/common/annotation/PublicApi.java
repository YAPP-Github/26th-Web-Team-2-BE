package com.yapp.backend.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JWT 인증이 필요하지 않은 공개 API를 명시하는 어노테이션
 * 
 * 이 어노테이션이 붙은 메서드는 JwtFilter에서 인증을 건너뛰고,
 * 누구나 접근할 수 있는 공개 엔드포인트로 처리됩니다.
 * 
 * 사용 예시:
 * - OAuth 로그인 엔드포인트
 * - 리프레시 토큰 재발급 API
 * - 공개 데이터 조회 API
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicApi {
    
    /**
     * 공개 API에 대한 설명
     * 
     * @return 해당 API가 공개된 이유나 용도 설명
     */
    String description() default "";
}
