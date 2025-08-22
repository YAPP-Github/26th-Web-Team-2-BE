package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.AccommodationDocs;
import com.yapp.backend.controller.dto.request.AccommodationMemoUpdateRequest;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationCountResponse;
import com.yapp.backend.controller.dto.response.AccommodationDeleteResponse;
import com.yapp.backend.controller.dto.response.AccommodationMemoUpdateResponse;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.common.annotation.RequirePermission;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.AccommodationService;

import com.yapp.backend.service.authorization.UserTripBoardAuthorizationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
     * 권한 : 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @RequirePermission(value = RequirePermission.PermissionType.TRIP_BOARD_ACCESS, paramName = "tripBoardId")
    @GetMapping("/search")
    public ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationByTripBoardIdAndUserId(
            @RequestParam Long tripBoardId,
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "saved_at_desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // userId 파라미터가 있는 경우 해당 사용자의 권한도 확인
        if (userId != null) {
            log.debug("대상 사용자 권한 검증 시작 - 대상사용자: {}, 보드ID: {}", userId, tripBoardId);
            authorizationService.validateTripBoardAccessOrThrow(userId, tripBoardId);
        }

        AccommodationPageResponse response = accommodationService
                .findAccommodationsByTripBoardId(tripBoardId, page, size, userId, sort);
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * 숙소 개수 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소 개수만 조회할 수 있습니다.
     * 권한 : 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @RequirePermission(value = RequirePermission.PermissionType.TRIP_BOARD_ACCESS, paramName = "tripBoardId")
    @GetMapping("/count")
    public ResponseEntity<StandardResponse<AccommodationCountResponse>> getAccommodationCountByTripBoardId(
            @RequestParam Long tripBoardId,
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long count = accommodationService.countAccommodationsByTripBoardId(tripBoardId, userId);
        AccommodationCountResponse accommodationCountResponse = new AccommodationCountResponse(count);
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, accommodationCountResponse));
    }

    /**
     * 숙소 카드 등록 API
     * 인증된 사용자가 속한 여행보드에만 숙소를 등록할 수 있습니다.
     * 권한 : 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @RequirePermission(value = RequirePermission.PermissionType.TRIP_BOARD_ACCESS, requestBodyField = "tripBoardId")
    @PostMapping("/register")
    public ResponseEntity<StandardResponse<AccommodationRegisterResponse>> registerAccommodationCard(
            @RequestBody AccommodationRegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        AccommodationRegisterResponse response = accommodationService.registerAccommodationCard(request,
                userDetails.getUserId());
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * 숙소 단건 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소만 조회할 수 있습니다.
     * 권한 : 숙소가 등록된 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @RequirePermission(value = RequirePermission.PermissionType.ACCOMMODATION_ACCESS, paramName = "accommodationId")
    @GetMapping("/{accommodationId}")
    public ResponseEntity<StandardResponse<AccommodationResponse>> getAccommodationById(
            @PathVariable Long accommodationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        AccommodationResponse response = accommodationService.findAccommodationById(accommodationId);
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * 숙소 삭제 API
     * 인증된 사용자가 본인이 등록한 숙소만 삭제할 수 있습니다.
     * 권한 : 숙소 소유자 (ACCOMMODATION_DELETE)
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @RequirePermission(value = RequirePermission.PermissionType.ACCOMMODATION_DELETE, paramName = "accommodationId")
    @DeleteMapping("/{accommodationId}")
    public ResponseEntity<StandardResponse<AccommodationDeleteResponse>> deleteAccommodation(
            @PathVariable Long accommodationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        AccommodationDeleteResponse response = accommodationService.deleteAccommodation(accommodationId,
                userDetails.getUserId());
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * 숙소 메모 수정 API
     * 인증된 사용자가 속한 여행보드의 숙소 메모를 수정할 수 있습니다.
     * 권한 : 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @RequirePermission(value = RequirePermission.PermissionType.ACCOMMODATION_ACCESS, paramName = "accommodationId")
    @PatchMapping("/{accommodationId}/memo")
    public ResponseEntity<StandardResponse<AccommodationMemoUpdateResponse>> updateAccommodationMemo(
            @PathVariable Long accommodationId,
            @RequestBody AccommodationMemoUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        AccommodationMemoUpdateResponse response = accommodationService.updateAccommodationMemo(
                accommodationId, request.getMemo());
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }
}