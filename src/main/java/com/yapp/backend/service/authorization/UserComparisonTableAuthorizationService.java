package com.yapp.backend.service.authorization;

import com.yapp.backend.service.model.ComparisonTable;

/**
 * 사용자 - 비교 테이블 권한 검증 서비스
 */
public interface UserComparisonTableAuthorizationService {


    /**
     * 비교 테이블 읽기 권한을 검증합니다. (엔티티 객체 사용)
     * @param comparisonTable 비교 테이블 엔티티
     * @param userId 사용자 ID
     */
    void validateReadPermission(ComparisonTable comparisonTable, Long userId);


    /**
     * 비교 테이블 수정 권한을 검증합니다. (엔티티 객체 사용)
     * @param comparisonTable 비교 테이블 엔티티
     * @param userId 사용자 ID
     */
    void validateUpdatePermission(ComparisonTable comparisonTable, Long userId);


    /**
     * 비교 테이블 삭제 권한을 검증합니다. (엔티티 객체 사용)
     * @param comparisonTable 비교 테이블 엔티티
     * @param userId 사용자 ID
     */
    void validateDeletePermission(ComparisonTable comparisonTable, Long userId);
}
