package com.yapp.backend.service.authorization;

import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.service.model.Accommodation;

/**
 * 사용자 - 숙소 권한 검증 서비스
 * 숙소에 대한 세부 권한 검증을 담당합니다.
 */
public interface UserAccommodationAuthorizationService {

    /**
     * 숙소 삭제 권한을 검증합니다.
     * 사용자가 해당 숙소의 소유자인지 확인합니다.
     * 
     * @param userId          사용자 ID
     * @param accommodationId 숙소 ID
     */
    void validateAccommodationDeleteOrThrow(Long userId, Long accommodationId);


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
