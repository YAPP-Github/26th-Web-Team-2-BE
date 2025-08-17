package com.yapp.backend.service.authorization;

import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.service.model.Accommodation;

/**
 * 사용자-여행보드 권한 검증을 위한 서비스 인터페이스
 */
public interface UserTripBoardAuthorizationService {
    /**
     * 사용자의 여행보드 접근 권한을 검증하고, 권한이 없으면 예외 발생
     */
    void validateTripBoardAccessOrThrow(Long userId, Long tripBoardId);

    /**
     * 사용자의 숙소 접근 권한을 검증하고, 권한이 없으면 예외 발생
     */
    void validateAccommodationAccessOrThrow(Long userId, Long accommodationId);

    /**
     * 사용자의 여행 보드 삭제 권한을 검증하고, 권한이 없으면 예외 발생
     */
    void validateTripBoardDeleteOrThrow(Long userId, Long tripBoardId);

    /**
     * 특정 숙소가 해당 여행보드에 속하는지 검증합니다. (도메인 객체 기반)
     * 이미 조회된 도메인 객체를 사용하여 중복 DB 조회를 방지합니다.
     * 
     * @param accommodation 검증할 숙소 도메인 객체
     * @param tripBoardId 검증할 여행보드 ID
     * @throws UserAuthorizationException 숙소가 해당 여행보드에 속하지 않는 경우
     */
    void validateAccommodationBelongsToTripBoardOrThrow(Accommodation accommodation, Long tripBoardId);
}