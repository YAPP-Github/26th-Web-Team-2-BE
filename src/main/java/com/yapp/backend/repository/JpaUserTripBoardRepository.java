package com.yapp.backend.repository;

import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.repository.enums.TripBoardRole;
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
         * 초대 코드 존재 여부 확인
         */
        boolean existsByInvitationCode(String invitationCode);

        /**
         * 사용자 ID와 여행 보드 ID로 매핑 정보 조회
         */
        Optional<UserTripBoardEntity> findByUserIdAndTripBoardId(Long userId, Long tripBoardId);

        /**
         * 활성화된 초대 코드로 매핑 정보 조회
         */
        Optional<UserTripBoardEntity> findByInvitationCodeAndInvitationActiveTrue(String invitationCode);

    /**
     * 초대 코드로 매핑 정보 조회
     */
    Optional<UserTripBoardEntity> findByInvitationCode(String invitationCode);

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
         * 여러 여행 보드의 모든 참여자를 한 번에 조회 (IN 절)
         */
        @Query("SELECT utb FROM UserTripBoardEntity utb " +
                        "JOIN FETCH utb.user u " +
                        "WHERE utb.tripBoard.id IN :tripBoardIds")
        List<UserTripBoardEntity> findByTripBoardIdsWithUser(@Param("tripBoardIds") List<Long> tripBoardIds);

        /**
         * 여행보드의 특정 역할 참여자를 생성일 오름차순으로 조회 (다음 OWNER 후보 조회용)
         */
        List<UserTripBoardEntity> findByTripBoardIdAndRoleOrderByCreatedAtAsc(Long tripBoardId, TripBoardRole role);

        /**
         * 사용자와 여행보드 간의 매핑 삭제
         */
        void deleteByUserIdAndTripBoardId(Long userId, Long tripBoardId);

        /**
         * 특정 여행보드의 모든 사용자-여행보드 매핑을 삭제 (여행보드 완전 삭제용)
         */
        void deleteByTripBoardId(Long tripBoardId);

        /**
         * tripBoardId로 존재 여부만 확인합니다.
         */
        boolean existsByTripBoardId(Long tripBoardId);

        /**
         * 특정 여행보드의 모든 참여자를 사용자 정보와 함께 조회 (상세조회용)
         */
        @Query("SELECT utb FROM UserTripBoardEntity utb " +
                "JOIN FETCH utb.user u " +
                "JOIN FETCH utb.tripBoard tb " +
                "WHERE utb.tripBoard.id = :tripBoardId " +
                "ORDER BY utb.role DESC, utb.createdAt ASC")
        List<UserTripBoardEntity> findByTripBoardIdWithUser(@Param("tripBoardId") Long tripBoardId);
}