package com.yapp.backend.service.authorization.impl;

import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.repository.JpaAccommodationRepository;
import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.TripBoardRepository;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.service.authorization.UserTripBoardAuthorizationService;
import com.yapp.backend.service.model.TripBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자-여행보드 권한 검증 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTripBoardAuthorizationServiceImpl implements UserTripBoardAuthorizationService {

    private final TripBoardRepository tripBoardRepository;
    private final JpaUserTripBoardRepository userTripBoardRepository;
    private final JpaAccommodationRepository accommodationRepository;

    @Override
    public void validateTripBoardAccessOrThrow(Long userId, Long tripBoardId) {
        log.info("여행보드 접근 권한 검증 시작 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);


        // 리소스 유효 검사
        TripBoard tripBoard = tripBoardRepository.findByIdOrThrow(tripBoardId);

        try {
            if (!hasAccessToTripBoard(userId, tripBoardId)) {
                log.warn("권한 없는 여행보드 접근 시도 차단 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);
                throw new UserAuthorizationException(userId, tripBoardId);
            }

            log.info("여행보드 접근 권한 검증 통과 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);
        } catch (UserAuthorizationException e) {
            throw e;
        } catch (Exception e) {
            log.error("여행보드 접근 권한 검증 중 예외 발생 - 사용자 ID: {}, 보드 ID: {}, 오류: {}",
                    userId, tripBoardId, e.getMessage(), e);
            throw new UserAuthorizationException(userId, tripBoardId);
        }
    }

    @Override
    public void validateAccommodationAccessOrThrow(Long userId, Long accommodationId) {
        log.info("숙소 접근 권한 검증 시작 - 사용자 ID: {}, 숙소 ID: {}", userId, accommodationId);

        if (userId == null || accommodationId == null) {
            log.warn("숙소 접근 권한 검증 실패 - 사유: null 파라미터, userId={}, accommodationId={}",
                    userId, accommodationId);
            throw UserAuthorizationException.forAccommodation(userId, accommodationId);
        }

        try {
            // 1. 숙소 정보 조회
            AccommodationEntity accommodation = accommodationRepository.findByAccommodationId(accommodationId);
            if (accommodation == null) {
                log.warn("숙소 접근 권한 검증 실패 - 사유: 숙소를 찾을 수 없음, accommodationId={}", accommodationId);
                throw UserAuthorizationException.forAccommodation(userId, accommodationId);
            }

            // 2. 해당 숙소가 속한 보드에 대한 사용자 권한 확인
            Long tripBoardId = accommodation.getTripBoardId();
            boolean hasAccess = hasAccessToTripBoard(userId, tripBoardId);

            if (!hasAccess) {
                log.warn("숙소 접근 권한 검증 실패 - 사용자 ID: {}, 숙소 ID: {}, 보드 ID: {}",
                        userId, accommodationId, tripBoardId);
                throw UserAuthorizationException.forAccommodation(userId, accommodationId);
            }

            log.info("숙소 접근 권한 검증 성공 - 사용자 ID: {}, 숙소 ID: {}, 보드 ID: {}",
                    userId, accommodationId, tripBoardId);

        } catch (UserAuthorizationException e) {
            // 이미 로깅된 예외를 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("숙소 접근 권한 검증 중 예외 발생 - 사용자 ID: {}, 숙소 ID: {}, 오류: {}",
                    userId, accommodationId, e.getMessage(), e);
            throw UserAuthorizationException.forAccommodation(userId, accommodationId);
        }
    }

    @Override
    public void validateTripBoardDeleteOrThrow(Long userId, Long tripBoardId) {
        // 리소스 유효 검사
        TripBoard tripBoard = tripBoardRepository.findByIdOrThrow(tripBoardId);
        // 여행 보드의 생성자 확인
        Long ownerId = tripBoard.getCreatedBy() != null ? tripBoard.getCreatedBy().getId() : null;
        if (ownerId == null || !ownerId.equals(userId)) {
            throw new UserAuthorizationException(userId, tripBoardId);
        }
    }

    private boolean hasAccessToTripBoard(Long userId, Long tripBoardId) {
        log.info("권한 검증 시작 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);

        if (userId == null || tripBoardId == null) {
            log.warn("권한 검증 실패 - 사유: null 파라미터, userId={}, boardId={}", userId, tripBoardId);
            return false;
        }

        try {
            boolean hasAccess = userTripBoardRepository.findByUserIdAndTripBoardId(userId, tripBoardId)
                    .isPresent();

            if (hasAccess) {
                log.info("권한 검증 성공 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);
            } else {
                log.warn("권한 검증 실패 - 사용자 ID: {}, 보드 ID: {}, 사유: 보드 멤버가 아님", userId, tripBoardId);
            }

            return hasAccess;

        } catch (Exception e) {
            return false;
        }
    }
}