package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.UserTripBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserTripBoardRepository extends JpaRepository<UserTripBoardEntity, Long> {
    /**
     * 초대 링크 존재 여부 확인
     */
    boolean existsByInvitationUrl(String invitationUrl);

    /**
     * 사용자 ID와 여행 보드 ID로 매핑 정보 조회
     */
    Optional<UserTripBoardEntity> findByUserIdAndTripBoardId(Long userId, Long tripBoardId);

    /**
     * 활성화된 초대 링크로 매핑 정보 조회
     */
    Optional<UserTripBoardEntity> findByInvitationUrlAndInvitationActiveTrue(String invitationUrl);

    /**
     * 여행 보드의 참여자 수 조회
     */
    long countByTripBoardId(Long tripBoardId);
}