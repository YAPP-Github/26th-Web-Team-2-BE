package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.InvalidDestinationException;
import com.yapp.backend.common.exception.InvalidTravelPeriodException;
import com.yapp.backend.common.exception.TripBoardCreationException;
import com.yapp.backend.common.exception.TripBoardParticipantLimitExceededException;
import com.yapp.backend.common.util.InvitationLinkGenerator;
import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.repository.JpaTripBoardRepository;
import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.repository.enums.TripBoardRole;
import com.yapp.backend.repository.mapper.TripBoardMapper;
import com.yapp.backend.repository.mapper.UserMapper;
import com.yapp.backend.service.TripBoardService;
import com.yapp.backend.service.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 여행 보드 도메인 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TripBoardServiceImpl implements TripBoardService {

    private static final int MAX_PARTICIPANTS = 10;

    private final JpaTripBoardRepository tripBoardRepository;
    private final JpaUserTripBoardRepository userTripBoardRepository;
    private final UserRepository userRepository;
    private final TripBoardMapper tripBoardMapper;
    private final InvitationLinkGenerator invitationLinkGenerator;
    private final UserMapper userMapper;

    /**
     * 여행 보드 생성
     * 생성자를 OWNER 역할로 자동 등록하고 초대 링크를 생성합니다.
     */
    @Override
    @Transactional
    public TripBoardCreateResponse createTripBoard(TripBoardCreateRequest request, Long userId) {
        try {
            log.info("여행 보드 생성 시작 - 사용자 ID: {}, 보드명: {}, 여행지: {}",
                    userId, request.getBoardName(), request.getDestination());

            // 1. 입력 데이터 유효성 검증
            validateTravelPeriod(request.getStartDate(), request.getEndDate());
            validateDestination(request.getDestination());

            // 2. 사용자 조회
            User user = userRepository.findByIdOrThrow(userId);
            UserEntity userEntity = userMapper.domainToEntity(user);

            // 3. 여행 보드 엔티티 생성
            TripBoardEntity tripBoardEntity = TripBoardEntity.builder()
                    .boardName(request.getBoardName())
                    .destination(request.getDestination())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .createdBy(userEntity)
                    .updatedBy(userEntity)
                    .build();

            // 4. 여행 보드 저장
            TripBoardEntity savedTripBoard = tripBoardRepository.save(tripBoardEntity);
            log.debug("여행 보드 저장 완료 - ID: {}", savedTripBoard.getId());

            // 5. 생성자용 고유 초대 링크 생성
            String invitationUrl = invitationLinkGenerator.generateUniqueInvitationUrl();
            log.debug("초대 링크 생성 완료: {}", invitationUrl);

            // 6. 생성자를 OWNER 역할로 자동 등록
            UserTripBoardEntity userTripBoardEntity = UserTripBoardEntity.builder()
                    .user(userEntity)
                    .tripBoard(savedTripBoard)
                    .invitationUrl(invitationUrl)
                    .invitationActive(true)
                    .role(TripBoardRole.OWNER)
                    .build();

            UserTripBoardEntity savedUserTripBoard = userTripBoardRepository.save(userTripBoardEntity);
            log.debug("사용자-여행보드 매핑 저장 완료 - ID: {}", savedUserTripBoard.getId());

            // 7. 응답 생성
            TripBoardCreateResponse response = TripBoardCreateResponse.builder()
                    .boardId(savedTripBoard.getId())
                    .boardName(savedTripBoard.getBoardName())
                    .destination(savedTripBoard.getDestination())
                    .travelPeriod(savedTripBoard.getFormattedTravelPeriod())
                    .startDate(savedTripBoard.getStartDate())
                    .endDate(savedTripBoard.getEndDate())
                    .invitationUrl(invitationUrl)
                    .invitationActive(true)
                    .creator(TripBoardCreateResponse.UserInfo.builder()
                            .id(user.getId())
                            .nickname(user.getNickname())
                            .email(user.getEmail())
                            .profileImage(user.getProfileImage())
                            .build())
                    .createdAt(savedTripBoard.getCreatedAt())
                    .build();

            log.info("여행 보드 생성 완료 - 보드 ID: {}, 사용자 ID: {}", savedTripBoard.getId(), userId);
            return response;

        } catch (TripBoardParticipantLimitExceededException e) {
            log.error("여행 보드 생성 실패 - 참여자 수 한계 초과: 사용자 ID: {}", userId);
            throw e;
        } catch (DataAccessException e) {
            log.error("여행 보드 생성 중 데이터베이스 오류 발생 - 사용자 ID: {}", userId, e);
            throw new TripBoardCreationException();
        } catch (Exception e) {
            log.error("여행 보드 생성 중 예상치 못한 오류 발생 - 사용자 ID: {}", userId, e);
            throw new TripBoardCreationException();
        }
    }

    /**
     * 여행 기간 유효성 검증
     * 출발일이 도착일보다 늦을 수 없습니다.
     */
    private void validateTravelPeriod(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            log.warn("유효하지 않은 여행 기간 - 출발일: {}, 도착일: {}", startDate, endDate);
            throw new InvalidTravelPeriodException();
        }
    }

    /**
     * 여행지 유효성 검증
     * 한글, 영문, 공백만 허용됩니다.
     */
    private void validateDestination(String destination) {
        if (!destination.matches("^[가-힣a-zA-Z\\s]+$")) {
            log.warn("유효하지 않은 여행지 형식 - 여행지: {}", destination);
            throw new InvalidDestinationException();
        }
    }
}