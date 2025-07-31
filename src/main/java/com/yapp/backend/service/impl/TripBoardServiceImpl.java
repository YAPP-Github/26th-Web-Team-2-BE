package com.yapp.backend.service.impl;

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
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.User;
import com.yapp.backend.service.model.UserTripBoard;
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
            log.info("여행 보드 생성 시작 - 사용자 ID: {}, 보드명: {}", userId, request.getBoardName());

            // 1. 사용자 조회
            User user = userRepository.findByIdOrThrow(userId);
            UserEntity userEntity = userMapper.domainToEntity(user);

            // 2. 여행 보드 엔티티 생성
            TripBoardEntity tripBoardEntity = TripBoardEntity.builder()
                    .boardName(request.getBoardName())
                    .createdBy(userEntity)
                    .updatedBy(userEntity)
                    .build();

            // 3. 여행 보드 저장
            TripBoardEntity savedTripBoard = tripBoardRepository.save(tripBoardEntity);
            log.debug("여행 보드 저장 완료 - ID: {}", savedTripBoard.getId());

            // 4. 참여자 수 제한 검증 (현재는 생성자만 있으므로 1명)
            long currentParticipantCount = userTripBoardRepository.countByTripBoardId(savedTripBoard.getId());
            if (currentParticipantCount >= MAX_PARTICIPANTS) {
                log.warn("참여자 수 한계 초과 - 현재: {}, 최대: {}", currentParticipantCount, MAX_PARTICIPANTS);
                throw new TripBoardParticipantLimitExceededException();
            }

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
            throw new TripBoardCreationException("데이터베이스 오류로 인해 여행 보드 생성에 실패했습니다.");
        } catch (Exception e) {
            log.error("여행 보드 생성 중 예상치 못한 오류 발생 - 사용자 ID: {}", userId, e);
            throw new TripBoardCreationException("여행 보드 생성 중 오류가 발생했습니다.");
        }
    }
}