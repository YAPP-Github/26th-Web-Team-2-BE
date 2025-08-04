package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.UserTripBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * 사용자가 참여한 여행 보드 목록을 페이징하여 조회 (최신순 정렬)
     * 생성일 내림차순, ID 내림차순으로 정렬
     */
    @Query("SELECT utb FROM UserTripBoardEntity utb " +
            "JOIN FETCH utb.tripBoard tb " +
            "WHERE utb.user.id = :userId " +
            "ORDER BY tb.createdAt DESC, tb.id DESC")
    Page<UserTripBoardEntity> findByUserIdOrderByTripBoardCreatedAtDescIdDesc(@Param("userId") Long userId,
            Pageable pageable);

    /**
     * 사용자가 참여한 여행 보드 개수 조회
     */
    @Query("SELECT COUNT(utb) FROM UserTripBoardEntity utb WHERE utb.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 특정 여행 보드의 모든 참여자 조회
     */
    @Query("SELECT utb FROM UserTripBoardEntity utb " +
            "JOIN FETCH utb.user u " +
            "WHERE utb.tripBoard.id = :tripBoardId")
    List<UserTripBoardEntity> findByTripBoardIdWithUser(@Param("tripBoardId") Long tripBoardId);
}