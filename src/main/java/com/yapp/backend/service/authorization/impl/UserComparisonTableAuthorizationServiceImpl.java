package com.yapp.backend.service.authorization.impl;

import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.service.authorization.UserComparisonTableAuthorizationService;
import com.yapp.backend.service.authorization.UserTripBoardAuthorizationService;
import com.yapp.backend.service.model.ComparisonTable;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserComparisonTableAuthorizationServiceImpl implements UserComparisonTableAuthorizationService {

    private final UserTripBoardAuthorizationService tripBoardAuthorizationService;


    @Override
    public void validateReadPermission(ComparisonTable comparisonTable, Long userId) {
        log.debug("비교 테이블 읽기 권한 검증 시작 (엔티티 사용) - tableId: {}, userId: {}", comparisonTable.getId(), userId);
        
        // 여행보드 멤버 권한 검증
        tripBoardAuthorizationService.validateTripBoardAccessOrThrow(userId, comparisonTable.getTripBoardId());
        
        log.debug("비교 테이블 읽기 권한 검증 성공 - tableId: {}, userId: {}", comparisonTable.getId(), userId);
    }


    @Override
    public void validateUpdatePermission(ComparisonTable comparisonTable, Long userId) {
        log.debug("비교 테이블 수정 권한 검증 시작 (엔티티 사용) - tableId: {}, userId: {}", comparisonTable.getId(), userId);
        
        // 여행보드 멤버 권한 검증
        tripBoardAuthorizationService.validateTripBoardAccessOrThrow(userId, comparisonTable.getTripBoardId());
        
        log.debug("비교 테이블 수정 권한 검증 성공 - tableId: {}, userId: {}", comparisonTable.getId(), userId);
    }


    @Override
    public void validateDeletePermission(ComparisonTable comparisonTable, Long userId) {
        log.debug("비교 테이블 삭제 권한 검증 시작 (엔티티 사용) - tableId: {}, userId: {}", comparisonTable.getId(), userId);
        
        // 비교 테이블 생성자만 삭제 가능
        if (!Objects.equals(comparisonTable.getCreatedById(), userId)) {
            log.warn("비교 테이블 삭제 권한 없음 - tableId: {}, userId: {}, createdById: {}", 
                    comparisonTable.getId(), userId, comparisonTable.getCreatedById());
            throw new UserAuthorizationException(ErrorCode.INVALID_USER_AUTHORIZATION);
        }
        
        log.debug("비교 테이블 삭제 권한 검증 성공 - tableId: {}, userId: {}", comparisonTable.getId(), userId);
    }

}
