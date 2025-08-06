package com.yapp.backend.service;

/**
 * 사용자-여행보드 권한 검증을 위한 서비스 인터페이스
 */
public interface UserTripBoardAuthorizationService {

    /**
     * 사용자가 특정 여행보드에 접근 권한이 있는지 확인
     */
    boolean hasAccessToTripBoard(Long userId, Long boardId);

    /**
     * 사용자의 여행보드 접근 권한을 검증하고, 권한이 없으면 예외 발생
     */
    void validateTripBoardAccess(Long userId, Long boardId);

    /**
     * 사용자의 숙소 접근 권한을 검증하고, 권한이 없으면 예외 발생
     */
    void validateAccommodationAccess(Long userId, Long accommodationId);
}