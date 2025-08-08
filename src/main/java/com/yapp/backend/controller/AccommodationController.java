package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.AccommodationDocs;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationCountResponse;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.common.annotation.RequirePermission;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.AccommodationService;

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

    /**
     * 숙소 목록 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소 목록만 조회할 수 있습니다.
	 * 권한 : 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @RequirePermission(value = RequirePermission.PermissionType.TRIP_BOARD_ACCESS, paramName = "boardId")
    @GetMapping("/search")
    public ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationByBoardIdAndUserId(
            @RequestParam Long boardId,
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "saved_at_desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 권한 검증은 AOP에서 자동으로 수행됨
        AccommodationPageResponse response = accommodationService.findAccommodationsByBoardId(boardId, page, size,
                userId, sort);

        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * 숙소 개수 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소 개수만 조회할 수 있습니다.
	 * 권한 : 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @RequirePermission(value = RequirePermission.PermissionType.TRIP_BOARD_ACCESS, paramName = "boardId")
    @GetMapping("/count")
    public ResponseEntity<StandardResponse<AccommodationCountResponse>> getAccommodationCountByBoardId(
            @RequestParam Long boardId,
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 권한 검증은 AOP에서 자동으로 수행됨
        Long count = accommodationService.countAccommodationsByBoardId(boardId, userId);
        AccommodationCountResponse accommodationCountResponse = new AccommodationCountResponse(count);

        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, accommodationCountResponse));
    }

    /**
     * 숙소 카드 등록 API
     * 인증된 사용자가 속한 여행보드에만 숙소를 등록할 수 있습니다.
	 * 권한 : 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @RequirePermission(value = RequirePermission.PermissionType.TRIP_BOARD_ACCESS, requestBodyField = "boardId")
    @PostMapping("/register")
    public ResponseEntity<StandardResponse<AccommodationRegisterResponse>> registerAccommodationCard(
            @RequestBody AccommodationRegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 권한 검증은 AOP에서 자동으로 수행됨
        AccommodationRegisterResponse response = accommodationService.registerAccommodationCard(request, userDetails.getUserId());

        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * 숙소 단건 조회 API
     * 인증된 사용자가 속한 여행보드의 숙소만 조회할 수 있습니다.
     * 권한 : 숙소가 등록된 여행 보드 참여자 - OWNER / MEMBER
     */
    @Override
    @RequirePermission(value = RequirePermission.PermissionType.ACCOMMODATION_ACCESS, paramName = "accommodationId")
    @GetMapping("/{accommodationId}")
    public ResponseEntity<StandardResponse<AccommodationResponse>> getAccommodationById(
            @PathVariable Long accommodationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        // 권한 검증은 AOP에서 자동으로 수행됨
        AccommodationResponse response = accommodationService.findAccommodationById(accommodationId);

        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }
}