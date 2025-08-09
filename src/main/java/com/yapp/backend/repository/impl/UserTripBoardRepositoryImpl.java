package com.yapp.backend.repository.impl;

import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.UserTripBoardRepository;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.repository.enums.TripBoardRole;
import com.yapp.backend.repository.mapper.UserTripBoardMapper;
import com.yapp.backend.service.model.UserTripBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자-여행보드 매핑 Repository 구현체
 */
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
                .collect(Collectors.toList());
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
    public boolean existsByTripBoardId(Long tripBoardId) {
        return jpaUserTripBoardRepository.countByTripBoardId(tripBoardId) > 0;
    }
}