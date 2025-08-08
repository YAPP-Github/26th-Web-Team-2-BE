package com.yapp.backend.service.authorization.impl;

import com.yapp.backend.common.exception.ErrorCode;
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
    public void validateTripBoardAccessOrThrow(Long userId, Long boardId) {
        log.info("여행보드 접근 권한 검증 시작 - 사용자 ID: {}, 보드 ID: {}", userId, boardId);


        // 리소스 유효 검사
        TripBoard tripBoard = tripBoardRepository.findByIdOrThrow(boardId);

        try {
            if (!hasAccessToTripBoard(userId, boardId)) {
                log.warn("권한 없는 여행보드 접근 시도 차단 - 사용자 ID: {}, 보드 ID: {}", userId, boardId);
                throw new UserAuthorizationException(userId, boardId);
            }

            log.info("여행보드 접근 권한 검증 통과 - 사용자 ID: {}, 보드 ID: {}", userId, boardId);
        } catch (UserAuthorizationException e) {
            // 이미 로깅된 예외를 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("여행보드 접근 권한 검증 중 예외 발생 - 사용자 ID: {}, 보드 ID: {}, 오류: {}",
                    userId, boardId, e.getMessage(), e);
            throw new UserAuthorizationException(userId, boardId);
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
            Long boardId = accommodation.getBoardId();
            boolean hasAccess = hasAccessToTripBoard(userId, boardId);

            if (!hasAccess) {
                log.warn("숙소 접근 권한 검증 실패 - 사용자 ID: {}, 숙소 ID: {}, 보드 ID: {}",
                        userId, accommodationId, boardId);
                throw UserAuthorizationException.forAccommodation(userId, accommodationId);
            }

            log.info("숙소 접근 권한 검증 성공 - 사용자 ID: {}, 숙소 ID: {}, 보드 ID: {}",
                    userId, accommodationId, boardId);

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
    public void validateTripBoardDeleteOrThrow(Long userId, Long boardId) {
        // 리소스 유효 검사
        TripBoard tripBoard = tripBoardRepository.findByIdOrThrow(boardId);
        // 여행 보드의 생성자 확인
        if (!tripBoard.getCreatedBy().getId().equals(userId)) {
            throw new UserAuthorizationException(ErrorCode.INVALID_USER_AUTHORIZATION);
        }
    }

    private boolean hasAccessToTripBoard(Long userId, Long boardId) {
        log.info("권한 검증 시작 - 사용자 ID: {}, 보드 ID: {}", userId, boardId);

        if (userId == null || boardId == null) {
            log.warn("권한 검증 실패 - 사유: null 파라미터, userId={}, boardId={}", userId, boardId);
            return false;
        }

        try {
            boolean hasAccess = userTripBoardRepository.findByUserIdAndTripBoardId(userId, boardId)
                    .isPresent();

            if (hasAccess) {
                log.info("권한 검증 성공 - 사용자 ID: {}, 보드 ID: {}", userId, boardId);
            } else {
                log.warn("권한 검증 실패 - 사용자 ID: {}, 보드 ID: {}, 사유: 보드 멤버가 아님", userId, boardId);
            }

            return hasAccess;

        } catch (Exception e) {
            return false;
        }
    }
}