package com.yapp.backend.repository.impl;

import com.yapp.backend.repository.TripBoardRepository;
import com.yapp.backend.repository.JpaTripBoardRepository;
import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.JpaAccommodationRepository;
import com.yapp.backend.repository.JpaComparisonTableRepository;
import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.repository.mapper.TripBoardMapper;
import com.yapp.backend.repository.mapper.UserTripBoardMapper;
import com.yapp.backend.repository.mapper.UserMapper;
import com.yapp.backend.service.dto.ParticipantProfile;
import com.yapp.backend.service.dto.TripBoardSummary;
import com.yapp.backend.service.model.TripBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    private final JpaAccommodationRepository jpaAccommodationRepository;
    private final JpaComparisonTableRepository jpaComparisonTableRepository;

    private final TripBoardMapper tripBoardMapper;
    private final UserTripBoardMapper userTripBoardMapper;
    private final UserMapper userMapper;

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

        // IN 절을 사용하여 한 번의 쿼리로 모든 참여자 정보 조회
        List<UserTripBoardEntity> userTripBoards = jpaUserTripBoardRepository.findByTripBoardIdsWithUser(tripBoardIds);

        // UserTripBoardEntity를 ParticipantProfile로 변환
        return userTripBoards.stream()
                .map(userTripBoardMapper::entityToParticipantProfile)
                .collect(Collectors.toList());
    }

    @Override
    public TripBoard updateTripBoard(TripBoard tripBoard) {
        // 기존 엔티티를 조회합니다
        TripBoardEntity existingEntity = jpaTripBoardRepository.findById(tripBoard.getId())
                .orElseThrow(() -> new RuntimeException("여행보드를 찾을 수 없습니다."));

        // 기존 엔티티의 필드를 업데이트합니다
        existingEntity.updateTripBoard(
                tripBoard.getBoardName(),
                tripBoard.getDestination(),
                tripBoard.getStartDate(),
                tripBoard.getEndDate(),
                userMapper.domainToEntity(tripBoard.getUpdatedBy()));

        // 변경된 엔티티를 저장합니다 (더티 체킹에 의해 자동으로 UPDATE 쿼리 실행)
        TripBoardEntity updatedEntity = jpaTripBoardRepository.save(existingEntity);
        return tripBoardMapper.entityToDomain(updatedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaTripBoardRepository.deleteById(id);
    }

    @Override
    public Optional<TripBoard> findByIdAndCreatedById(Long tripBoardId, Long createdById) {
        return jpaTripBoardRepository.findByIdAndCreatedById(tripBoardId, createdById)
                .map(tripBoardMapper::entityToDomain);
    }

    @Override
    @Transactional
    public void deleteTripBoardCompletely(Long tripBoardId) {
        // 여행보드와 관련된 모든 데이터를 순서대로 삭제합니다.
        // 1. 비교표 삭제 (ComparisonAccommodation 매핑도 cascade로 함께 삭제됨)
        jpaComparisonTableRepository.deleteByTripBoardId(tripBoardId);

        // 2. 숙소 삭제
        jpaAccommodationRepository.deleteByBoardId(tripBoardId);

        // 3. 사용자-여행보드 매핑 삭제
        jpaUserTripBoardRepository.deleteByTripBoardId(tripBoardId);

        // 4. 여행보드 삭제
        jpaTripBoardRepository.deleteById(tripBoardId);
    }

}