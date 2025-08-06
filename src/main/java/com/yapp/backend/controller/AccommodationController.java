package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.AccommodationDocs;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationCountResponse;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.AccommodationService;
import com.yapp.backend.service.UserTripBoardAuthorizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 숙소 도메인 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
@Validated
public class AccommodationController implements AccommodationDocs {

    private final AccommodationService accommodationService;
    private final UserTripBoardAuthorizationService authorizationService;

    /**
     * 숙소 목록 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소 목록만 조회할 수 있습니다.
     */
    @Override
    @GetMapping("/search")
    public ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationByBoardIdAndUserId(
            @RequestParam Long boardId,
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "saved_at_desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long whoAmI = userDetails.getUserId();

        try {
            // 1. 현재 사용자의 boardId 접근 권한 확인
            log.debug("숙소 목록 조회 권한 검증 시작 - 요청자: {}, 보드ID: {}", whoAmI, boardId);
            authorizationService.validateTripBoardAccess(whoAmI, boardId);

            // 2. userId 파라미터가 있는 경우 해당 사용자의 권한도 확인
            if (userId != null) {
                log.debug("대상 사용자 권한 검증 시작 - 대상사용자: {}, 보드ID: {}", userId, boardId);
                authorizationService.validateTripBoardAccess(userId, boardId);
            }

            // 3. 권한 확인 후 기존 로직 실행
            AccommodationPageResponse response = accommodationService.findAccommodationsByBoardId(boardId, page, size,
                    userId, sort);

            return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));

        } catch (Exception e) {
            log.error("숙소 목록 조회 API 실패 - 요청자: {}, 보드ID: {}, 오류: {}",
                    whoAmI, boardId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 숙소 개수 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소 개수만 조회할 수 있습니다.
     */
    @Override
    @GetMapping("/count")
    public ResponseEntity<StandardResponse<AccommodationCountResponse>> getAccommodationCountByBoardId(
            @RequestParam Long boardId,
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long whoAmI = userDetails.getUserId();

        try {
            // 1. 현재 사용자의 boardId 접근 권한 확인
            log.debug("숙소 개수 조회 권한 검증 시작 - 요청자: {}, 보드ID: {}", whoAmI, boardId);
            authorizationService.validateTripBoardAccess(whoAmI, boardId);

            // 2. userId 파라미터가 있는 경우 해당 사용자의 권한도 확인
            if (userId != null) {
                log.debug("대상 사용자 권한 검증 시작 - 대상사용자: {}, 보드ID: {}", userId, boardId);
                authorizationService.validateTripBoardAccess(userId, boardId);
            }

            // 3. 권한 확인 후 기존 로직 실행
            Long count = accommodationService.countAccommodationsByBoardId(boardId, userId);
            AccommodationCountResponse accommodationCountResponse = new AccommodationCountResponse(count);

            log.info("숙소 개수 조회 API 성공 - 요청자: {}, 보드ID: {}, 개수: {}", whoAmI, boardId, count);

            return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, accommodationCountResponse));

        } catch (Exception e) {
            log.error("숙소 개수 조회 API 실패 - 요청자: {}, 보드ID: {}, 오류: {}", whoAmI, boardId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 숙소 카드 등록 API
     * 인증된 사용자가 속한 여행보드에만 숙소를 등록할 수 있습니다.
     */
    @Override
    @PostMapping("/register")
    public ResponseEntity<StandardResponse<AccommodationRegisterResponse>> registerAccommodationCard(
            @RequestBody AccommodationRegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long whoAmI = userDetails.getUserId();

        try {
            // 1. 현재 사용자의 boardId 접근 권한 확인
            log.debug("숙소 등록 권한 검증 시작 - 요청자: {}, 보드ID: {}", whoAmI, request.getBoardId());
            authorizationService.validateTripBoardAccess(whoAmI, request.getBoardId());

            // 2. 권한 확인 후 사용자 ID를 포함하여 숙소 등록
            AccommodationRegisterResponse response = accommodationService.registerAccommodationCard(request, whoAmI);

            log.info("숙소 등록 API 성공 - 요청자: {}, 보드ID: {}, 등록된숙소ID: {}",
                    whoAmI, request.getBoardId(), response.getAccommodationId());

            return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));

        } catch (Exception e) {
            log.error("숙소 등록 API 실패 - 요청자: {}, 보드ID: {}, 오류: {}",
                    whoAmI, request.getBoardId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 숙소 단건 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소만 조회할 수 있습니다.
     */
    @Override
    @GetMapping("/{accommodationId}")
    public ResponseEntity<StandardResponse<AccommodationResponse>> getAccommodationById(
            @PathVariable Long accommodationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long whoAmI = userDetails.getUserId();

        try {
            // 1. 현재 사용자의 해당 숙소 접근 권한 확인
            log.debug("숙소 단건 조회 권한 검증 시작 - 요청자: {}, 숙소ID: {}", whoAmI, accommodationId);
            authorizationService.validateAccommodationAccess(whoAmI, accommodationId);

            // 2. 권한 확인 후 기존 로직 실행
            AccommodationResponse response = accommodationService.findAccommodationById(accommodationId);

            log.info("숙소 단건 조회 API 성공 - 요청자: {}, 숙소ID: {}, 숙소명: {}",
                    whoAmI, accommodationId, response.getAccommodationName());

            return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));

        } catch (Exception e) {
            log.error("숙소 단건 조회 API 실패 - 요청자: {}, 숙소ID: {}, 오류: {}",
                    whoAmI, accommodationId, e.getMessage(), e);
            throw e;
        }
    }
}