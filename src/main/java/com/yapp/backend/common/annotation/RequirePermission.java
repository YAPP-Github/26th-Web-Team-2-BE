package com.yapp.backend.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 권한 검증을 위한 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * 검증할 권한 타입
     */
    PermissionType value();
    
    /**
     * 권한 검증에 사용할 파라미터 이름
     */
    String paramName() default "id";
    
    /**
     * RequestBody에서 ID를 추출할 필드명
     */
    String requestBodyField() default "";

    /**
     * 권한 타입 enum
     */
    enum PermissionType {
        // 여행보드 관련 권한
        TRIP_BOARD_ACCESS,      // 여행보드 접근 권한 (OWNER, MEMBER)
        TRIP_BOARD_MODIFY,      // 여행보드 수정 권한 (OWNER)
        TRIP_BOARD_DELETE,      // 여행보드 삭제 권한 (OWNER)

        // 숙소 관련 권한
        ACCOMMODATION_ACCESS,   // 숙소 접근 권한 (OWNER, MEMBER)
        ACCOMMODATION_MODIFY,   // 숙소 수정 권한 (OWNER, MEMBER)
        ACCOMMODATION_DELETE,   // 숙소 삭제 권한 (ONLY ACCOMMODATION CREATED_BY)

        // 비교표 관련 권한
        COMPARISON_TABLE_ACCESS, // 비교표 접근 권한 (OWNER, MEMBER)
        COMPARISON_TABLE_MODIFY, // 비교표 수정 권한 (OWNER, MEMBER)
        COMPARISON_TABLE_DELETE, // 비교표 삭제 권한 (OWNER, MEMBER)

        // 초대 관련 권한
        INVITATION,             // 초대 권한 (OWNER, MEMBER)
    }
} 