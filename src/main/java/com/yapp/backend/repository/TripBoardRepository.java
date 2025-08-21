package com.yapp.backend.repository;

import com.yapp.backend.service.dto.ParticipantProfile;
import com.yapp.backend.service.dto.TripBoardSummary;
import com.yapp.backend.service.model.TripBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 여행 보드 Repository 인터페이스
 */
public interface TripBoardRepository {

    /**
     * 여행 보드를 저장합니다.
     */
    TripBoard save(TripBoard tripBoard);

    /**
     * ID로 여행 보드를 조회합니다.
     */
    TripBoard findByIdOrThrow(Long id);

    /**
     * 사용자가 참여한 여행 보드 목록을 페이징하여 조회합니다.
     * 최신순 정렬 (생성일 내림차순, ID 내림차순)
     */
    Page<TripBoardSummary> findTripBoardsByUser(Long userId, Pageable pageable);

    /**
     * 여러 여행 보드의 참여자 프로필 정보를 효율적으로 조회합니다.
     * N+1 문제를 방지하기 위해 단일 쿼리로 모든 참여자 정보를 조회합니다.
     */
    List<ParticipantProfile> findParticipantsByTripBoardIds(List<Long> tripBoardIds);

    /**
     * 여행 보드를 업데이트합니다.
     */
    TripBoard updateTripBoard(TripBoard tripBoard);

    /**
     * 여행보드를 완전히 삭제합니다 (관련된 모든 데이터 포함).
     */
    void deleteTripBoardCompletely(Long tripBoardId);

    /**
     * ID로 여행 보드를 삭제합니다.
     */
    void deleteById(Long id);

    /**
     * 여행 보드 ID와 생성자 ID로 여행 보드를 조회합니다. (소유자 검증용)
     */
    Optional<TripBoard> findByIdAndCreatedById(Long tripBoardId, Long createdById);

    /**
     * 여행보드 존재 여부 확인
     */
    boolean existsById(Long tripBoardId);

    /**
     * ID로 여행보드를 조회합니다 (상세조회용)
     * 존재하지 않는 경우 Optional.empty() 반환
     */
    Optional<TripBoard> findById(Long id);

    /**
     * 특정 여행보드의 다음 비교표 번호를 가져옵니다.
     * @param tripBoardId 여행보드 ID
     * @return 다음 비교표 번호
     */
    int getNextComparisonTableNumber(Long tripBoardId);
}