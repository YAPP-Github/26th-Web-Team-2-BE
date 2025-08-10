package com.yapp.backend.repository;

import com.yapp.backend.service.model.UserTripBoard;
import com.yapp.backend.repository.enums.TripBoardRole;

import java.util.List;
import java.util.Optional;

/**
 * 사용자-여행보드 매핑 Repository 인터페이스
 */
public interface UserTripBoardRepository {

    /**
     * 사용자 ID와 여행보드 ID로 매핑 정보 조회 (중복 참여 검증에도 활용)
     */
    Optional<UserTripBoard> findByUserIdAndTripBoardId(Long userId, Long tripBoardId);

    /**
     * 여행보드의 참여자 수 조회
     */
    long countByTripBoardId(Long tripBoardId);

    /**
     * 여행보드의 특정 역할 참여자를 생성일 오름차순으로 조회 (다음 OWNER 후보 조회용)
     */
    List<UserTripBoard> findByTripBoardIdAndRoleOrderByCreatedAtAsc(Long tripBoardId, TripBoardRole role);

    /**
     * 사용자와 여행보드 간의 매핑 삭제
     */
    void deleteByUserIdAndTripBoardId(Long userId, Long tripBoardId);

    /**
     * 여행보드 ID로 해당 보드의 모든 사용자 매핑을 삭제합니다.
     */
    void deleteByTripBoardId(Long tripBoardId);

    /**
     * 사용자-여행보드 매핑 정보 저장/업데이트
     */
    UserTripBoard save(UserTripBoard userTripBoard);

    /**
     * 초대 코드로 사용자-여행보드 매핑 정보 조회
     */
    Optional<UserTripBoard> findByInvitationCode(String invitationCode);
}