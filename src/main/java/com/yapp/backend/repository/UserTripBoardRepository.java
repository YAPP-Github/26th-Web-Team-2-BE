package com.yapp.backend.repository;

/**
 * 사용자-여행보드 매핑 Repository 인터페이스
 */
public interface UserTripBoardRepository {

    /**
     * 여행보드 ID로 해당 보드의 모든 사용자 매핑을 삭제합니다.
     */
    void deleteByTripBoardId(Long tripBoardId);
}