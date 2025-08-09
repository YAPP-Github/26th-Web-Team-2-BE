package com.yapp.backend.service.authorization;

import com.yapp.backend.common.annotation.RequirePermission;
import com.yapp.backend.common.annotation.RequirePermission.PermissionType;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.UserAuthorizationException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 권한 검증 담당 서비스
 * 각 리소스별 권한 타입에 따라 권한 검증 메소드를 매핑합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final UserTripBoardAuthorizationService userTripBoardAuthorizationService;


    /**
     * 권한 검증 분기 메소드
     * 리소스에 따라 권한 검증 메소드를 호출합니다.
     * @param annotation        검증 리소스 정보
     * @param resourceIds       검증할 리소스 ID들
     * @param userId            현재 사용자 ID
     */
    public void validatePermission(
            RequirePermission annotation,
            Map<String, Long> resourceIds,
            Long userId
    ) {
        PermissionType permissionType = annotation.value();
        Long resourceId = resourceIds.get(annotation.paramName());
        if(resourceId == null)
            resourceId = resourceIds.get(annotation.requestBodyField());

        log.info("권한 검증 시작: type={}, resourceIds={}, userId={}", permissionType, resourceIds, userId);
        switch (permissionType) {
            case TRIP_BOARD_ACCESS:
            case TRIP_BOARD_MODIFY:
            case TRIP_BOARD_DELETE:
                validateTripBoardPermission(permissionType, resourceId, userId);
                break;

            case COMPARISON_TABLE_ACCESS:
            case COMPARISON_TABLE_MODIFY:
            case COMPARISON_TABLE_DELETE:
                validateComparisonTablePermission(permissionType, resourceId, userId);
                break;

            case ACCOMMODATION_ACCESS:
            case ACCOMMODATION_MODIFY:
            case ACCOMMODATION_DELETE:
                validateAccommodationPermission(permissionType, resourceId, userId);
                break;

            default:
                throw new UserAuthorizationException(ErrorCode.INVALID_USER_AUTHORIZATION);
        }

        log.info("권한 검증 완료: type={}, resourceIds={}, userId={}", permissionType, resourceIds, userId);
    }


    /**
     * 여행보드 권한 검증 진입 메서드
     *
     * @param permissionType    검증할 권한 타입
     * @param boardId           여행 보드 ID
     * @param userId            현재 사용자 ID
     */
    private void validateTripBoardPermission(
            PermissionType permissionType,
            Long boardId,
            Long userId
    ) {
        log.info("validateTripBoardPermission: {}", boardId);

        /**
         // 여행 보드 수정 권한은 해당 여행 보드에 참여한 사용자 모두에게 있습니다. (OWNER / MEMBER)
         *  여행보드 삭제는 소유자만 가능합니다. (ONLY OWNER)
         */
        switch (permissionType) {
            case TRIP_BOARD_ACCESS:
            case TRIP_BOARD_MODIFY:
                userTripBoardAuthorizationService.validateTripBoardAccessOrThrow(userId, boardId);
                break;
            case TRIP_BOARD_DELETE:
                userTripBoardAuthorizationService.validateTripBoardDeleteOrThrow(userId, boardId);
                break;
        }
    }

    /**
     * 비교 테이블 권한 검증 진입 메서드
     * 세부적인 권한 타입에 따라 검증 메서드를 호출합니다.
     * @param permissionType        검증할 권한 타입
     * @param comparisonTableId     비교 테이블 ID
     * @param userId                현재 사용자 ID
     */
    private void validateComparisonTablePermission(
            PermissionType permissionType,
            Long comparisonTableId,
            Long userId
    ) {
        log.info("validateComparisonTablePermission: {}", comparisonTableId);

        /**
         * 비교 테이블 조회 및 수정은 여행 보드 참여한 사용자 모두 가능합니다.
         * TODO : 비교 테이블 삭제 권한은 비교 테이블을 생성한 사용자에게 있습니다. (기획 확인 필요)
         * TODO : 비교 테이블 리소스 유효 검사는 권한 검증 메서드 내부에서 합니다.
         */
        switch (permissionType) {
            case COMPARISON_TABLE_ACCESS:
            case COMPARISON_TABLE_MODIFY:
                // TODO : 비교 테이블 조회 + 수정 권한 검증 메소드
                break;
            case COMPARISON_TABLE_DELETE:
                // TODO : 비교 테이블 삭제 권한 검증 메소드
                break;
        }

    }


    /**
     * 숙소 권한 검증 진입 메서드
     * 세부적인 권한 타입에 따라 검증 메서드를 호출합니다.
     * @param permissionType        검증할 권한 타입
     * @param accommodationId       숙소 ID
     * @param userId                현재 사용자 ID
     */
    private void validateAccommodationPermission(
            PermissionType permissionType,
            Long accommodationId,
            Long userId
    ) {
        log.info("validateAccommodationPermission: {}", accommodationId);

        /**
         * 숙소 조회는 해당 숙소가 등록된 여행 보드의 참여자 모두가 가능합니다.
         * 숙소 유효성 검사는 권한 검증 메소드 내부에서 진행됩니다.
         * TODO : 숙소 삭제는 해당 숙소를 등록한 사용자만 가능합니다. (기획 확인 필요)
         */
        switch (permissionType) {
            case ACCOMMODATION_ACCESS:
                userTripBoardAuthorizationService.validateAccommodationAccessOrThrow(userId, accommodationId);
                break;
            case ACCOMMODATION_DELETE:
                // TODO : 숙소 삭제 권한 검증 메소드
                break;
        }
    }

}