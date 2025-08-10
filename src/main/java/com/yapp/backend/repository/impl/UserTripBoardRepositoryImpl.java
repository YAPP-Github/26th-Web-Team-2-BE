package com.yapp.backend.repository.impl;

import com.yapp.backend.common.exception.InactiveInvitationUrlException;
import com.yapp.backend.common.exception.InvalidInvitationUrlException;
import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.UserTripBoardRepository;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.repository.enums.TripBoardRole;
import com.yapp.backend.repository.mapper.UserTripBoardMapper;
import com.yapp.backend.service.dto.ParticipantProfile;
import com.yapp.backend.service.model.UserTripBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자-여행보드 매핑 Repository 구현체
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserTripBoardRepositoryImpl implements UserTripBoardRepository {

    private final JpaUserTripBoardRepository jpaUserTripBoardRepository;
    private final UserTripBoardMapper userTripBoardMapper;

    @Override
    public Optional<UserTripBoard> findByUserIdAndTripBoardId(Long userId, Long tripBoardId) {
        return jpaUserTripBoardRepository.findByUserIdAndTripBoardId(userId, tripBoardId)
                .map(userTripBoardMapper::entityToDomain);
    }

    @Override
    public long countByTripBoardId(Long tripBoardId) {
        return jpaUserTripBoardRepository.countByTripBoardId(tripBoardId);
    }

    @Override
    public List<UserTripBoard> findByTripBoardIdAndRoleOrderByCreatedAtAsc(Long tripBoardId, TripBoardRole role) {
        return jpaUserTripBoardRepository.findByTripBoardIdAndRoleOrderByCreatedAtAsc(tripBoardId, role)
                .stream()
                .map(userTripBoardMapper::entityToDomain)
                .toList();
    }

    @Override
    public void deleteByUserIdAndTripBoardId(Long userId, Long tripBoardId) {
        jpaUserTripBoardRepository.deleteByUserIdAndTripBoardId(userId, tripBoardId);
    }

    @Override
    public void deleteByTripBoardId(Long tripBoardId) {
        jpaUserTripBoardRepository.deleteByTripBoardId(tripBoardId);
    }

    @Override
    public UserTripBoard save(UserTripBoard userTripBoard) {
        UserTripBoardEntity entity = userTripBoardMapper.domainToEntity(userTripBoard);
        UserTripBoardEntity savedEntity = jpaUserTripBoardRepository.save(entity);
        return userTripBoardMapper.entityToDomain(savedEntity);
    }

    @Override
    public UserTripBoard findByInvitationCodeOrThrow(String invitationCode) {
        log.debug("초대 코드 유효성 검증 시작 - 초대 코드: {}", invitationCode);

        // 초대 코드로 UserTripBoard 조회
        UserTripBoard userTripBoard = jpaUserTripBoardRepository.findByInvitationCode(invitationCode)
                .map(userTripBoardMapper::entityToDomain)
                .orElseThrow(() -> {
                    log.warn("유효하지 않은 초대 코드 - 초대 코드: {}", invitationCode);
                    return new InvalidInvitationUrlException();
                });

        // 초대 코드 활성화 상태 확인
        if (!userTripBoard.getInvitationActive()) {
            log.warn("비활성화된 초대 코드 - 초대 코드: {}", invitationCode);
            throw new InactiveInvitationUrlException();
        }

        log.debug("초대 코드 유효성 검증 완료 - 보드 ID: {}", userTripBoard.getTripBoard().getId());
        return userTripBoard;
    }

    @Override
    public List<ParticipantProfile> findParticipantsByTripBoardId(Long tripBoardId) {
        return jpaUserTripBoardRepository.findByTripBoardIdWithUser(tripBoardId)
                .stream()
                .map(userTripBoardMapper::entityToParticipantProfile)
                .collect(Collectors.toList());
    }
}