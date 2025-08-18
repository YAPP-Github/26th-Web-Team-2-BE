package com.yapp.backend.service.authorization.impl;

import com.yapp.backend.common.exception.AccommodationNotFoundException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.InvalidRequestException;
import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.service.authorization.UserAccommodationAuthorizationService;
import com.yapp.backend.service.authorization.UserTripBoardAuthorizationService;
import com.yapp.backend.service.model.Accommodation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자-숙소 권한 검증 서비스 구현체
 * 숙소에 대한 소유자 권한 및 접근 권한을 검증합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccommodationAuthorizationServiceImpl implements UserAccommodationAuthorizationService {

    private final AccommodationRepository accommodationRepository;
    private final UserTripBoardAuthorizationService userTripBoardAuthorizationService;

    @Override
    public void validateAccommodationDeleteOrThrow(Long userId, Long accommodationId) {
        log.info("숙소 삭제 권한 검증 시작 - 사용자 ID: {}, 숙소 ID: {}", userId, accommodationId);

        if (userId == null || accommodationId == null) {
            log.warn("숙소 삭제 권한 검증 실패 - 사유: null 파라미터, userId={}, accommodationId={}",
                    userId, accommodationId);
            throw new UserAuthorizationException(ErrorCode.ACCOMMODATION_DELETE_FORBIDDEN);
        }

        try {
            // 1. 숙소 조회
            Accommodation accommodation = accommodationRepository.findByIdOrThrow(accommodationId);

            // 2. 숙소 소유자 검증 (createdBy 필드 확인)
            Long ownerId = accommodation.getCreatedBy();
            if (ownerId == null || !ownerId.equals(userId)) {
                log.warn("숙소 삭제 권한 검증 실패 - 사용자 ID: {}, 숙소 ID: {}, 소유자 ID: {}",
                        userId, accommodationId, ownerId);
                throw new UserAuthorizationException(ErrorCode.ACCOMMODATION_DELETE_FORBIDDEN);
            }

            log.info("숙소 삭제 권한 검증 성공 - 사용자 ID: {}, 숙소 ID: {}", userId, accommodationId);

        } catch (AccommodationNotFoundException | UserAuthorizationException e) {
            // 이미 로깅된 예외를 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("숙소 삭제 권한 검증 중 예외 발생 - 사용자 ID: {}, 숙소 ID: {}, 오류: {}",
                    userId, accommodationId, e.getMessage(), e);
            throw new UserAuthorizationException(ErrorCode.ACCOMMODATION_DELETE_FORBIDDEN);
        }
    }


    @Override
    public void validateAccommodationBelongsToTripBoardOrThrow(Accommodation accommodation, Long tripBoardId) {
        log.info("숙소-여행보드 소속 검증 시작 (도메인 객체 기반) - 숙소 ID: {}, 여행보드 ID: {}",
                accommodation.getId(), tripBoardId);

        // 숙소가 해당 여행보드에 속하는지 확인
        Long accommodationTripBoardId = accommodation.getTripBoardId();
        if (!tripBoardId.equals(accommodationTripBoardId)) {
            log.warn("숙소-여행보드 소속 검증 실패 - 사유: 다른 여행보드의 숙소, 숙소 ID: {}, 요청 보드 ID: {}, 실제 보드 ID: {}",
                    accommodation.getId(), tripBoardId, accommodationTripBoardId);
            throw new InvalidRequestException(ErrorCode.INVALID_ACCOMMODATION, "여행보드에 생성된 숙소 정보가 아닙니다");
        }

        log.info("숙소-여행보드 소속 검증 성공 (도메인 객체 기반) - 숙소 ID: {}, 여행보드 ID: {}",
                accommodation.getId(), tripBoardId);
    }
}