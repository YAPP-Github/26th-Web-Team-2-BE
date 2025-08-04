package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.TripBoardDocs;
import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.TripBoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 여행 보드 도메인 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip-boards")
@Validated
public class TripBoardController implements TripBoardDocs {

    private final TripBoardService tripBoardService;

    /**
     * 여행 보드 생성 API
     */
    @Override
    @PostMapping("/register")
    public ResponseEntity<StandardResponse<TripBoardCreateResponse>> createTripBoard(
            @RequestBody @Valid TripBoardCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // JWT 인증을 통한 현재 사용자 정보 추출
        Long userId = userDetails.getUserId();

        // 여행 보드 생성
        TripBoardCreateResponse response = tripBoardService.createTripBoard(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse<>(ResponseType.SUCCESS, response));
    }
}