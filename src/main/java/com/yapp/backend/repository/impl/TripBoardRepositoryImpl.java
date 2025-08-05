package com.yapp.backend.repository.impl;

import com.yapp.backend.repository.TripBoardRepository;
import com.yapp.backend.repository.JpaTripBoardRepository;
import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.repository.mapper.TripBoardMapper;
import com.yapp.backend.repository.mapper.UserTripBoardMapper;
import com.yapp.backend.service.dto.ParticipantProfile;
import com.yapp.backend.service.dto.TripBoardSummary;
import com.yapp.backend.service.model.TripBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 여행 보드 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class TripBoardRepositoryImpl implements TripBoardRepository {

    private final JpaTripBoardRepository jpaTripBoardRepository;
    private final JpaUserTripBoardRepository jpaUserTripBoardRepository;

    private final TripBoardMapper tripBoardMapper;
    private final UserTripBoardMapper userTripBoardMapper;

    @Override
    public TripBoard save(TripBoard tripBoard) {
        TripBoardEntity entity = tripBoardMapper.domainToEntity(tripBoard);
        TripBoardEntity savedEntity = jpaTripBoardRepository.save(entity);
        return tripBoardMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<TripBoard> findById(Long id) {
        return jpaTripBoardRepository.findById(id)
                .map(tripBoardMapper::entityToDomain);
    }

    @Override
    public Page<TripBoardSummary> findTripBoardsByUser(Long userId, Pageable pageable) {
        // JpaRepository 메서드를 사용하여 사용자가 참여한 여행 보드 조회 (최신순 정렬)
        Page<UserTripBoardEntity> userTripBoardPage = jpaUserTripBoardRepository
                .findByUserIdOrderByTripBoardCreatedAtDescIdDesc(userId, pageable);

        // UserTripBoardEntity를 TripBoardSummary로 변환
        List<TripBoardSummary> content = userTripBoardPage.getContent().stream()
                .map(userTripBoardMapper::entityToTripBoardSummary)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, userTripBoardPage.getTotalElements());
    }

    @Override
    public List<ParticipantProfile> findParticipantsByTripBoardIds(List<Long> tripBoardIds) {
        if (tripBoardIds == null || tripBoardIds.isEmpty()) {
            return List.of();
        }

        // JpaRepository를 사용하여 참여자 정보 조회
        List<UserTripBoardEntity> userTripBoards = tripBoardIds.stream()
                .flatMap(tripBoardId -> jpaUserTripBoardRepository.findByTripBoardIdWithUser(tripBoardId).stream())
                .toList();

        // UserTripBoardEntity를 ParticipantProfile로 변환
        return userTripBoards.stream()
                .map(userTripBoardMapper::entityToParticipantProfile)
                .collect(Collectors.toList());
    }

}