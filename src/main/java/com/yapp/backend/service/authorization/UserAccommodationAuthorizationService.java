package com.yapp.backend.service.authorization;

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
     * 숙소 접근 권한을 검증합니다.
     * 사용자가 해당 숙소에 접근할 수 있는지 확인합니다.
     * 
     * @param userId          사용자 ID
     * @param accommodationId 숙소 ID
     */
    void validateAccommodationAccessOrThrow(Long userId, Long accommodationId);
}
