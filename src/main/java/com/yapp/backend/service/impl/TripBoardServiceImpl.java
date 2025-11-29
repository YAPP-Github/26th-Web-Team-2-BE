package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.DuplicateTripBoardParticipationException;
import com.yapp.backend.common.exception.InactiveInvitationUrlException;
import com.yapp.backend.common.exception.InvalidDestinationException;
import com.yapp.backend.common.exception.InvalidInvitationUrlException;
import com.yapp.backend.common.exception.InvalidPagingParameterException;
import com.yapp.backend.common.exception.InvalidTravelPeriodException;
import com.yapp.backend.common.exception.TripBoardCreationException;
import com.yapp.backend.common.exception.TripBoardLeaveException;
import com.yapp.backend.common.exception.TripBoardDeleteException;
import com.yapp.backend.common.exception.TripBoardNotFoundException;
import com.yapp.backend.common.exception.TripBoardParticipantLimitExceededException;
import com.yapp.backend.common.exception.TripBoardUpdateException;
import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.common.util.InvitationLinkGeneratorUtil;
import com.yapp.backend.common.util.PageUtil;
import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.request.TripBoardUpdateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.controller.dto.response.TripBoardJoinResponse;
import com.yapp.backend.controller.dto.response.TripBoardLeaveResponse;
import com.yapp.backend.controller.dto.response.TripBoardDeleteResponse;
import com.yapp.backend.controller.dto.response.TripBoardPageResponse;
import com.yapp.backend.controller.dto.response.TripBoardSummaryResponse;
import com.yapp.backend.controller.dto.response.TripBoardUpdateResponse;
import com.yapp.backend.controller.dto.response.InvitationToggleResponse;
import com.yapp.backend.controller.dto.response.InvitationCodeResponse;
import com.yapp.backend.controller.mapper.TripBoardSummaryMapper;
import com.yapp.backend.controller.mapper.TripBoardUpdateMapper;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.ComparisonTableRepository;
import com.yapp.backend.repository.JpaTripBoardRepository;
import com.yapp.backend.repository.JpaUserTripBoardRepository;
import com.yapp.backend.repository.TripBoardRepository;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.repository.UserTripBoardRepository;
import com.yapp.backend.repository.entity.TripBoardEntity;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.repository.entity.UserTripBoardEntity;
import com.yapp.backend.repository.enums.TripBoardRole;
import com.yapp.backend.repository.mapper.TripBoardMapper;
import com.yapp.backend.repository.mapper.UserMapper;
import com.yapp.backend.repository.mapper.UserTripBoardMapper;
import com.yapp.backend.service.TripBoardService;
import com.yapp.backend.service.dto.ParticipantProfile;
import com.yapp.backend.service.dto.TripBoardSummary;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.User;
import com.yapp.backend.service.model.UserTripBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 여행 보드 도메인 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TripBoardServiceImpl implements TripBoardService {

    private static final int MAX_PARTICIPANTS = 10;

    private final JpaTripBoardRepository jpaTripBoardRepository;
    private final JpaUserTripBoardRepository jpaUserTripBoardRepository;
    private final TripBoardRepository tripBoardRepository;
    private final UserRepository userRepository;
    private final UserTripBoardRepository userTripBoardRepository;
    private final AccommodationRepository accommodationRepository;
    private final ComparisonTableRepository comparisonTableRepository;

    private final UserMapper userMapper;
    private final TripBoardMapper tripBoardMapper;
    private final UserTripBoardMapper userTripBoardMapper;
    private final TripBoardSummaryMapper tripBoardSummaryMapper;
    private final TripBoardUpdateMapper tripBoardUpdateMapper;

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
            TripBoardEntity savedTripBoard = jpaTripBoardRepository.save(tripBoardEntity);
            log.debug("여행 보드 저장 완료 - ID: {}", savedTripBoard.getId());

            // 5. 생성자용 고유 초대 코드 생성
            String invitationCode = InvitationLinkGeneratorUtil.generateUniqueInvitationUrl();
            log.debug("초대 코드 생성 완료: {}", invitationCode);

            // 6. 생성자를 OWNER 역할로 자동 등록
            UserTripBoardEntity userTripBoardEntity = UserTripBoardEntity.builder()
                    .user(userEntity)
                    .tripBoard(savedTripBoard)
                    .invitationCode(invitationCode)
                    .invitationActive(true)
                    .role(TripBoardRole.OWNER)
                    .build();

            UserTripBoardEntity savedUserTripBoard = jpaUserTripBoardRepository.save(userTripBoardEntity);
            log.debug("사용자-여행보드 매핑 저장 완료 - ID: {}", savedUserTripBoard.getId());

            // 7. Entity를 Domain Model로 변환
            TripBoard tripBoardDomain = tripBoardMapper.entityToDomain(savedTripBoard);
            UserTripBoard userTripBoardDomain = userTripBoardMapper.entityToDomain(savedUserTripBoard);

            // 8. Domain Model을 이용해서 응답 DTO 생성
            TripBoardCreateResponse response = TripBoardCreateResponse.from(tripBoardDomain, userTripBoardDomain);

            log.info("여행 보드 생성 완료 - 보드 ID: {}, 사용자 ID: {}", tripBoardDomain.getId(), userId);
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

    /**
     * 사용자가 참여한 여행 보드 목록 조회
     * 페이징과 정렬을 지원하며, 최신순으로 정렬됩니다.
     */
    @Override
    @Transactional(readOnly = true)
    public TripBoardPageResponse getTripBoards(Long userId, Pageable pageable) {
        try {
            log.info("여행 보드 목록 조회 시작 - 사용자 ID: {}, 페이지: {}, 크기: {}",
                    userId, pageable.getPageNumber(), pageable.getPageSize());

            // 1. 페이징 파라미터 유효성 검증
            PageUtil.validatePagingParameters(pageable);

            // 2. Repository 계층에서 페이징된 여행보드 조회 (최신순 정렬)
            Page<TripBoardSummary> tripBoardPage = tripBoardRepository.findTripBoardsByUser(userId, pageable);

            // 3. 유저가 참여한 보드 ID 목록 조회
            List<Long> tripBoardIds = tripBoardPage.getContent().stream()
                    .map(TripBoardSummary::getTripBoardId)
                    .collect(Collectors.toList());

            // 4. 보드들에 참여한 모든 유저 ID 조회
            List<ParticipantProfile> participantProfiles = tripBoardRepository
                    .findParticipantsByTripBoardIds(tripBoardIds);

            // 4. 응답 DTO 변환
            List<TripBoardSummaryResponse> content = tripBoardSummaryMapper
                    .toResponseList(tripBoardPage.getContent(), participantProfiles);

            // 5. 무한 스크롤 응답 생성
            TripBoardPageResponse response = TripBoardPageResponse.builder()
                    .tripBoards(content)
                    .hasNext(tripBoardPage.hasNext())
                    .totalCnt(tripBoardPage.getTotalElements())
                    .build();

            log.info("여행 보드 목록 조회 완료 - 사용자 ID: {}, 조회된 개수: {}, 전체 개수: {}",
                    userId, content.size(), tripBoardPage.getTotalElements());

            return response;

        } catch (InvalidPagingParameterException e) {
            log.error("여행 보드 목록 조회 실패 - 잘못된 페이징 파라미터: 사용자 ID: {}", userId, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("여행 보드 목록 조회 중 데이터베이스 오류 발생 - 사용자 ID: {}", userId, e);
            throw new RuntimeException("여행 보드 목록 조회에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("여행 보드 목록 조회 중 예상치 못한 오류 발생 - 사용자 ID: {}", userId, e);
            throw new RuntimeException("여행 보드 목록 조회에 실패했습니다.", e);
        }
    }

    /**
     * 여행 보드 수정
     * 기존 여행보드의 기본 정보(보드 이름, 목적지, 여행 기간)를 수정합니다.
     */
    @Override
    @Transactional
    public TripBoardUpdateResponse updateTripBoard(Long tripBoardId, TripBoardUpdateRequest request, Long userId) {
        try {
            log.info("여행 보드 수정 시작 - 보드 ID: {}, 사용자 ID: {}, 보드명: {}, 여행지: {}",
                    tripBoardId, userId, request.getBoardName(), request.getDestination());

            // 1. 입력 데이터 유효성 검증
            validateTravelPeriod(request.getStartDate(), request.getEndDate());
            validateDestination(request.getDestination());

            // 2. 여행보드 존재 여부 확인
            tripBoardRepository.findByIdOrThrow(tripBoardId);

            // 3. 수정을 수행하는 사용자 조회
            User updatedByUser = userRepository.findByIdOrThrow(userId);

            // 4. Request DTO를 Domain 모델로 변환
            TripBoard tripBoardToUpdate = tripBoardUpdateMapper.requestToDomain(request, tripBoardId, updatedByUser);

            // 5. 여행보드 업데이트 실행
            TripBoard updatedTripBoard = tripBoardRepository.updateTripBoard(tripBoardToUpdate);
            log.debug("여행 보드 업데이트 완료 - 보드 ID: {}", updatedTripBoard.getId());

            // 6. Domain 모델을 Response DTO로 변환
            TripBoardUpdateResponse response = tripBoardUpdateMapper.domainToResponse(updatedTripBoard);

            log.info("여행 보드 수정 완료 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
            return response;

        } catch (TripBoardNotFoundException e) {
            log.error("여행 보드 수정 실패 - 보드를 찾을 수 없음: 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
            throw e;
        } catch (InvalidDestinationException | InvalidTravelPeriodException e) {
            log.error("여행 보드 수정 실패 - 입력 데이터 유효성 검증 실패: 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("여행 보드 수정 중 데이터베이스 오류 발생 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw new TripBoardUpdateException();
        } catch (Exception e) {
            log.error("여행 보드 수정 중 예상치 못한 오류 발생 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw new TripBoardUpdateException();
        }
    }

    /**
     * 여행 보드 삭제
     * 소유자만 삭제할 수 있으며, 관련된 모든 데이터를 cascade 방식으로 삭제합니다.
     */
    @Override
    @Transactional
    public TripBoardDeleteResponse deleteTripBoard(Long tripBoardId, Long userId) {
        try {
            log.info("여행 보드 삭제 시작 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);

            // 1. 여행보드 존재 여부 및 소유자 권한 검증
            validateOwnershipAndGetTripBoard(tripBoardId, userId);

            // 2. 관련 데이터 cascade 삭제 (순서 중요)
            deleteRelatedData(tripBoardId);

            // 3. 여행보드 삭제
            tripBoardRepository.deleteById(tripBoardId);
            log.debug("여행 보드 삭제 완료 - 보드 ID: {}", tripBoardId);

            // 4. 응답 생성
            TripBoardDeleteResponse response = TripBoardDeleteResponse.builder()
                    .tripBoardId(tripBoardId)
                    .build();

            log.info("여행 보드 삭제 완료 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
            return response;

        } catch (TripBoardNotFoundException | UserAuthorizationException e) {
            log.error("여행 보드 삭제 실패 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("여행 보드 삭제 중 데이터베이스 오류 발생 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw new TripBoardDeleteException();
        } catch (Exception e) {
            log.error("여행 보드 삭제 중 예상치 못한 오류 발생 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw new TripBoardDeleteException();
        }
    }

    /**
     * 여행보드 소유자 권한 검증 및 여행보드 조회
     */
    private void validateOwnershipAndGetTripBoard(Long tripBoardId, Long userId) {
        // 여행보드 존재 여부 확인
        TripBoard tripBoard = tripBoardRepository.findByIdOrThrow(tripBoardId);

        // 소유자 권한 검증 (createdBy 필드와 사용자 ID 비교)
        if (!tripBoard.getCreatedBy().getId().equals(userId)) {
            log.warn("권한 없는 사용자의 여행보드 삭제 시도 - 보드 ID: {}, 요청 사용자 ID: {}, 소유자 ID: {}",
                    tripBoardId, userId, tripBoard.getCreatedBy().getId());
            throw new UserAuthorizationException(userId, tripBoardId);
        }
    }

    /**
     * 여행보드와 관련된 모든 데이터를 순서에 따라 삭제
     * 삭제 순서: ComparisonAccommodation → ComparisonTable → Accommodation →
     * UserTripBoard
     */
    private void deleteRelatedData(Long tripBoardId) {
        log.debug("관련 데이터 삭제 시작 - 보드 ID: {}", tripBoardId);

        try {
            // 1. 비교표 삭제 (ComparisonAccommodation 매핑도 함께 삭제됨)
            comparisonTableRepository.deleteByTripBoardId(tripBoardId);
            log.debug("비교표 삭제 완료 - 보드 ID: {}", tripBoardId);

            // 2. 숙소 삭제
            accommodationRepository.deleteByTripBoardId(tripBoardId);
            log.debug("숙소 삭제 완료 - 보드 ID: {}", tripBoardId);

            // 3. 사용자-여행보드 매핑 삭제
            userTripBoardRepository.deleteByTripBoardId(tripBoardId);
            log.debug("사용자-여행보드 매핑 삭제 완료 - 보드 ID: {}", tripBoardId);

        } catch (Exception e) {
            log.error("관련 데이터 삭제 중 오류 발생 - 보드 ID: {}", tripBoardId, e);
            throw e;
        }
    }

    /**
     * 여행보드 나가기
     * OWNER가 나가는 경우 다음 MEMBER에게 권한을 이양하고, 마지막 참여자인 경우 여행보드를 완전 삭제합니다.
     * todo 치명적: 마지막 두 명이 동시에 나가면 ‘참여자 0명 보드’가 남을 수 있음 + removeResources NPE 위험
     */
    @Override
    @Transactional
    public TripBoardLeaveResponse leaveTripBoard(Long tripBoardId, Long userId, Boolean removeResources) {
        try {
            log.info("여행보드 나가기 시작 - 보드 ID: {}, 사용자 ID: {}, 리소스 제거: {}",
                    tripBoardId, userId, removeResources);

            // 1. 여행보드 존재 여부 확인
            if (!tripBoardRepository.existsById(tripBoardId)) {
                log.warn("존재하지 않는 여행보드 나가기 시도 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
                throw new TripBoardNotFoundException();
            }

            // 2. 사용자가 해당 여행보드의 참여자인지 확인
            Optional<UserTripBoard> userTripBoardOpt = userTripBoardRepository
                    .findByUserIdAndTripBoardId(userId, tripBoardId);

            if (userTripBoardOpt.isEmpty()) {
                log.warn("참여하지 않은 여행보드 나가기 시도 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
                throw new UserAuthorizationException();
            }

            UserTripBoard currentUserTripBoard = userTripBoardOpt.get();
            TripBoardRole currentUserRole = currentUserTripBoard.getRole();

            log.debug("사용자 역할 확인 - 사용자 ID: {}, 역할: {}", userId, currentUserRole);

            // 3. 전체 참여자 수 확인
            long totalParticipants = userTripBoardRepository.countByTripBoardId(tripBoardId);
            boolean isLastParticipant = totalParticipants == 1;

            log.debug("참여자 수 확인 - 전체 참여자: {}, 마지막 참여자 여부: {}", totalParticipants, isLastParticipant);

            // 4. 응답 객체 초기화
            TripBoardLeaveResponse.TripBoardLeaveResponseBuilder responseBuilder = TripBoardLeaveResponse.builder()
                    .tripBoardId(tripBoardId)
                    .leftAt(LocalDateTime.now());

            // 5. 마지막 참여자인 경우 여행보드 완전 삭제
            if (isLastParticipant) {
                log.info("마지막 참여자 나가기 - 여행보드 완전 삭제 시작: 보드 ID: {}", tripBoardId);
                tripBoardRepository.deleteTripBoardCompletely(tripBoardId);
                log.info("여행보드 완전 삭제 완료 - 보드 ID: {}", tripBoardId);
            } else {
                // 6. OWNER 권한 이양 처리 (OWNER인 경우)
                Long newOwnerId = null;
                if (currentUserRole == TripBoardRole.OWNER) {
                    newOwnerId = transferOwnership(tripBoardId, userId);
                    log.info("OWNER 권한 이양 완료 - 이전 OWNER: {}, 새 OWNER: {}", userId, newOwnerId);
                }

                // 7. 리소스 처리 (removeResources가 true인 경우)
                if (Boolean.TRUE.equals(removeResources)) {
                    deleteUserResources(tripBoardId, userId);
                    log.info("사용자 리소스 삭제 완료 - 사용자 ID: {}", userId);
                }

                // 8. 사용자-여행보드 매핑 삭제
                userTripBoardRepository.deleteByUserIdAndTripBoardId(userId, tripBoardId);
                log.debug("사용자-여행보드 매핑 삭제 완료 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);
            }

            TripBoardLeaveResponse response = responseBuilder.build();
            log.info("여행보드 나가기 완료 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
            return response;

        } catch (TripBoardNotFoundException | UserAuthorizationException e) {
            log.error("여행보드 나가기 실패 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("여행보드 나가기 중 데이터베이스 오류 발생 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw new TripBoardLeaveException();
        } catch (Exception e) {
            log.error("여행보드 나가기 중 예상치 못한 오류 발생 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw new TripBoardLeaveException();
        }
    }

    /**
     * OWNER 권한을 다음 MEMBER에게 이양합니다.
     * 가장 먼저 입장한 MEMBER를 찾아 OWNER로 변경합니다.
     */
    private Long transferOwnership(Long tripBoardId, Long currentOwnerId) {
        log.debug("OWNER 권한 이양 시작 - 보드 ID: {}, 현재 OWNER: {}", tripBoardId, currentOwnerId);

        // 가장 먼저 입장한 MEMBER 조회
        List<UserTripBoard> members = userTripBoardRepository
                .findByTripBoardIdAndRoleOrderByCreatedAtAsc(tripBoardId, TripBoardRole.MEMBER);

        if (members.isEmpty()) {
            log.error("OWNER 권한 이양 실패 - 다음 MEMBER가 없음: 보드 ID: {}", tripBoardId);
            throw new TripBoardLeaveException();
        }

        // 첫 번째 MEMBER를 OWNER로 변경
        UserTripBoard nextOwner = members.get(0);
        UserTripBoard updatedNextOwner = UserTripBoard.builder()
                .id(nextOwner.getId())
                .user(nextOwner.getUser())
                .tripBoard(nextOwner.getTripBoard())
                .invitationCode(nextOwner.getInvitationCode())
                .invitationActive(nextOwner.getInvitationActive())
                .role(TripBoardRole.OWNER)
                .createdAt(nextOwner.getCreatedAt())
                .build();

        userTripBoardRepository.save(updatedNextOwner);

        Long newOwnerId = nextOwner.getUser().getId();
        log.debug("OWNER 권한 이양 완료 - 새 OWNER: {}", newOwnerId);
        return newOwnerId;
    }

    /**
     * 사용자가 생성한 리소스(비교표, 숙소)를 삭제합니다.
     */
    private void deleteUserResources(Long tripBoardId, Long userId) {
        log.debug("사용자 리소스 삭제 시작 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);

        // 사용자가 생성한 비교표 삭제
        comparisonTableRepository.deleteByTripBoardIdAndCreatedById(tripBoardId, userId);
        log.debug("사용자 비교표 삭제 완료 - 사용자 ID: {}", userId);

        // 사용자가 등록한 숙소 삭제
        accommodationRepository.deleteByTripBoardIdAndCreatedById(tripBoardId, userId);
        log.debug("사용자 숙소 삭제 완료 - 사용자 ID: {}", userId);
    }

    /**
     * 초대 코드를 통해 여행 보드에 참여합니다.
     * 초대 코드 유효성 검증, 활성화 상태 확인, 중복 참여 검증을 수행한 후 새로운 참여자를 추가합니다.
     * 동시성 안전성을 위해 DataIntegrityViolationException을 처리합니다.
     */
    @Override
    @Transactional
    public TripBoardJoinResponse joinTripBoard(String invitationCode, Long userId) {
        try {
            log.info("여행 보드 참여 시작 - 사용자 ID: {}, 초대 코드: {}", userId, invitationCode);

            // 1. 초대 코드 유효성 검증 및 활성화 상태 확인
            UserTripBoard invitationUserTripBoard = validateInvitationCode(invitationCode);
            TripBoard tripBoard = invitationUserTripBoard.getTripBoard();
            Long tripBoardId = tripBoard.getId();

            log.debug("초대 코드 검증 완료 - 보드 ID: {}, 보드명: {}", tripBoardId, tripBoard.getBoardName());

            // 2. 중복 참여 검증
            validateDuplicateParticipation(userId, tripBoardId);

            // 3. 참여자 수 제한 확인 (비관적 락 적용)
            validateParticipantLimitWithLock(tripBoardId);

            // 4. 사용자 조회
            User user = userRepository.findByIdOrThrow(userId);

            // 5. 새로운 UserTripBoard 엔티티 생성
            UserTripBoard newUserTripBoard = createNewUserTripBoard(user, tripBoard);

            // 6. 사용자-보드 매핑 저장 (유니크 제약 조건 위반 시 예외 처리)
            UserTripBoard savedUserTripBoard;
            try {
                savedUserTripBoard = userTripBoardRepository.save(newUserTripBoard);
                log.debug("사용자-여행보드 매핑 저장 완료 - 매핑 ID: {}", savedUserTripBoard.getId());
            } catch (DataIntegrityViolationException e) {
                log.warn("중복 참여 시도 감지 (동시성) - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);
                throw new DuplicateTripBoardParticipationException();
            }

            // 7. 현재 참여자 수 조회
            int participantCount = (int) userTripBoardRepository.countByTripBoardId(tripBoardId);

            // 8. 응답 생성
            TripBoardJoinResponse response = TripBoardJoinResponse.from(
                    tripBoard,
                    participantCount,
                    savedUserTripBoard.getCreatedAt());

            log.info("여행 보드 참여 완료 - 사용자 ID: {}, 보드 ID: {}, 참여자 수: {}",
                    userId, tripBoardId, participantCount);
            return response;

        } catch (InvalidInvitationUrlException | InactiveInvitationUrlException
                | DuplicateTripBoardParticipationException | TripBoardParticipantLimitExceededException e) {
            log.error("여행 보드 참여 실패 - 사용자 ID: {}, 초대 코드: {}", userId, invitationCode, e);
            throw e;
        } catch (DataAccessException e) {
            log.error("여행 보드 참여 중 데이터베이스 오류 발생 - 사용자 ID: {}, 초대 코드: {}", userId, invitationCode, e);
            throw new RuntimeException("여행 보드 참여에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("여행 보드 참여 중 예상치 못한 오류 발생 - 사용자 ID: {}, 초대 코드: {}", userId, invitationCode, e);
            throw new RuntimeException("여행 보드 참여에 실패했습니다.", e);
        }
    }

    /**
     * 초대 코드 유효성 검증 및 활성화 상태 확인
     */
    private UserTripBoard validateInvitationCode(String invitationCode) {
        return userTripBoardRepository.findByInvitationCodeOrThrow(invitationCode);
    }

    /**
     * 중복 참여 검증
     */
    private void validateDuplicateParticipation(Long userId, Long tripBoardId) {
        log.debug("중복 참여 검증 시작 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);

        Optional<UserTripBoard> existingParticipation = userTripBoardRepository
                .findByUserIdAndTripBoardId(userId, tripBoardId);

        if (existingParticipation.isPresent()) {
            log.warn("중복 참여 시도 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);
            throw new DuplicateTripBoardParticipationException();
        }

        log.debug("중복 참여 검증 완료 - 사용자 ID: {}, 보드 ID: {}", userId, tripBoardId);
    }

    /**
     * 참여자 수 제한 확인 (동시성 안전성을 위한 락 적용)
     */
    private void validateParticipantLimitWithLock(Long tripBoardId) {
        log.debug("참여자 수 제한 확인 시작 (락 적용) - 보드 ID: {}", tripBoardId);

        // 여행보드 존재 여부 확인과 동시에 락 획득
        tripBoardRepository.findByIdOrThrow(tripBoardId);

        long currentParticipantCount = userTripBoardRepository.countByTripBoardId(tripBoardId);

        if (currentParticipantCount >= MAX_PARTICIPANTS) {
            log.warn("참여자 수 한계 초과 - 보드 ID: {}, 현재 참여자 수: {}, 최대 참여자 수: {}",
                    tripBoardId, currentParticipantCount, MAX_PARTICIPANTS);
            throw new TripBoardParticipantLimitExceededException();
        }

        log.debug("참여자 수 제한 확인 완료 - 보드 ID: {}, 현재 참여자 수: {}/{}",
                tripBoardId, currentParticipantCount, MAX_PARTICIPANTS);
    }

    /**
     * 새로운 UserTripBoard 엔티티 생성
     */
    private UserTripBoard createNewUserTripBoard(User user, TripBoard tripBoard) {
        log.debug("새로운 UserTripBoard 생성 시작 - 사용자 ID: {}, 보드 ID: {}", user.getId(), tripBoard.getId());

        // 새로운 참여자용 고유 초대 코드 생성
        String newInvitationCode = InvitationLinkGeneratorUtil.generateUniqueInvitationUrl();
        log.debug("새로운 초대 코드 생성 완료: {}", newInvitationCode);

        UserTripBoard newUserTripBoard = UserTripBoard.builder()
                .user(user)
                .tripBoard(tripBoard)
                .invitationCode(newInvitationCode)
                .invitationActive(true)
                .role(TripBoardRole.MEMBER)
                .build();

        log.debug("새로운 UserTripBoard 생성 완료 - 사용자 ID: {}, 보드 ID: {}, 역할: {}",
                user.getId(), tripBoard.getId(), TripBoardRole.MEMBER);
        return newUserTripBoard;
    }

    /**
     * 여행보드 상세 정보 조회
     * 참여자만 조회할 수 있으며, 보드의 기본 정보와 참여자 목록을 반환합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public TripBoardSummaryResponse getTripBoardDetail(Long tripBoardId, Long userId) {
        try {
            log.info("여행보드 상세조회 시작 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);

            // 1. 여행보드 존재 여부 검증
            if (!tripBoardRepository.existsById(tripBoardId)) {
                log.warn("존재하지 않는 여행보드 조회 시도 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
                throw new TripBoardNotFoundException();
            }

            // 2. 사용자 참여 권한 검증
            Optional<UserTripBoard> userTripBoardOpt = userTripBoardRepository
                    .findByUserIdAndTripBoardId(userId, tripBoardId);

            if (userTripBoardOpt.isEmpty()) {
                log.warn("참여하지 않은 여행보드 조회 시도 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId);
                throw new UserAuthorizationException(userId, tripBoardId);
            }

            UserTripBoard userTripBoard = userTripBoardOpt.get();
            TripBoardRole userRole = userTripBoard.getRole();

            log.debug("사용자 권한 확인 완료 - 사용자 ID: {}, 역할: {}", userId, userRole);

            // 3. 여행보드 기본 정보 조회
            TripBoard tripBoard = tripBoardRepository.findByIdOrThrow(tripBoardId);

            // 4. 참여자 목록 조회
            List<ParticipantProfile> participantProfiles = tripBoardRepository
                    .findParticipantsByTripBoardIds(List.of(tripBoardId));

            log.debug("참여자 정보 조회 완료 - 보드 ID: {}, 참여자 수: {}", tripBoardId, participantProfiles.size());

            // 5. 해당 여행보드의 전체 숙소 개수 조회
            Long accommodationCount = accommodationRepository.countByTripBoardId(tripBoardId, null);

            // 6. TripBoardSummary 객체 생성
            TripBoardSummary tripBoardSummary = TripBoardSummary.builder()
                    .tripBoardId(tripBoard.getId())
                    .boardName(tripBoard.getBoardName())
                    .destination(tripBoard.getDestination())
                    .startDate(tripBoard.getStartDate())
                    .endDate(tripBoard.getEndDate())
                    .travelPeriod(formatTravelPeriod(tripBoard.getStartDate(), tripBoard.getEndDate()))
                    .userRole(userRole)
                    .accommodationCount(accommodationCount.intValue())
                    .createdAt(tripBoard.getCreatedAt())
                    .updatedAt(tripBoard.getUpdatedAt())
                    .build();

            // 7. TripBoardSummaryResponse 객체 생성 및 반환
            TripBoardSummaryResponse response = tripBoardSummaryMapper
                    .toResponse(tripBoardSummary, participantProfiles);

            log.info("여행보드 상세조회 완료 - 보드 ID: {}, 사용자 ID: {}, 참여자 수: {}",
                    tripBoardId, userId, response.getParticipantCount());

            return response;

        } catch (TripBoardNotFoundException | UserAuthorizationException e) {
            log.error("여행보드 상세조회 실패 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw e;
        } catch (Exception e) {
            log.error("여행보드 상세조회 중 예상치 못한 오류 발생 - 보드 ID: {}, 사용자 ID: {}", tripBoardId, userId, e);
            throw new RuntimeException("여행보드 상세조회에 실패했습니다.", e);
        }
    }

    /**
     * 여행 기간을 "YY.MM.DD~MM.DD" 형식으로 포맷팅합니다.
     */
    private String formatTravelPeriod(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yy.MM.dd");

        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        // 같은 년도인 경우 끝날짜에서 년도 생략
        if (startDate.getYear() == endDate.getYear()) {
            String shortEndDate = endDate.format(java.time.format.DateTimeFormatter.ofPattern("MM.dd"));
            return formattedStartDate + "~" + shortEndDate;
        }

        return formattedStartDate + "~" + formattedEndDate;
    }

    /**
     * 여행 보드의 초대 링크 활성화 상태를 토글합니다. 현재 상태의 반대로 변경됩니다.
     */
    @Override
    @Transactional
    public InvitationToggleResponse toggleInvitationActive(Long tripBoardId, Long userId) {
        log.info("초대 링크 활성화 상태 토글 시작 - 보드 ID: {}, 사용자 ID: {}",
                tripBoardId, userId);

        // 1. 사용자가 해당 여행보드의 참여자인지 확인
        UserTripBoard userTripBoard = userTripBoardRepository
                .findByUserIdAndTripBoardId(userId, tripBoardId)
                .orElseThrow(() -> {
                    log.warn("참여하지 않은 여행보드 초대 링크 토글 시도 - 보드 ID: {}, 사용자 ID: {}", tripBoardId,
                            userId);
                    return new UserAuthorizationException();
                });

        // 2. 도메인 객체의 토글 메서드를 사용하여 상태 변경
        Boolean currentActive = userTripBoard.getInvitationActive();
        UserTripBoard toggledUserTripBoard = userTripBoard.toggleInvitationActive();
        Boolean newActive = toggledUserTripBoard.getInvitationActive();

        log.debug("초대 링크 상태 토글 - 보드 ID: {}, 사용자 ID: {}, 이전 상태: {}, 새 상태: {}",
                tripBoardId, userId, currentActive, newActive);

        // 3. 토글된 상태 저장
        UserTripBoard savedUserTripBoard = userTripBoardRepository.save(toggledUserTripBoard);

        log.info("초대 링크 활성화 상태 토글 완료 - 보드 ID: {}, 사용자 ID: {}, 새 상태: {}",
                tripBoardId, userId, newActive);

        return new InvitationToggleResponse(
                tripBoardId,
                savedUserTripBoard.getInvitationActive(),
                savedUserTripBoard.getInvitationCode()
        );
    }

    /**
     * 여행 보드에서 사용자의 초대 링크 정보를 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public InvitationCodeResponse getInvitationCode(Long tripBoardId, Long userId) {

        // 1. 사용자가 해당 여행보드의 참여자인지 확인
        UserTripBoard userTripBoard = userTripBoardRepository
                .findByUserIdAndTripBoardId(userId, tripBoardId)
                .orElseThrow(() -> {
                    log.warn("참여하지 않은 여행보드 초대 링크 조회 시도 - 보드 ID: {}, 사용자 ID: {}",
                            tripBoardId, userId);
                    return new UserAuthorizationException();
                });

        // 2. 응답 생성
        return new InvitationCodeResponse(
                tripBoardId,
                userTripBoard.getInvitationActive(),
                userTripBoard.getInvitationCode()
        );
    }

}