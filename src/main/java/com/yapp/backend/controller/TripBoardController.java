package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.TripBoardDocs;
import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.controller.dto.response.TripBoardPageResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.TripBoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 여행 보드 목록 조회 API
     */
    @Override
    @GetMapping("/search")
    public ResponseEntity<StandardResponse<TripBoardPageResponse>> getTripBoards(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // JWT 인증을 통한 현재 사용자 정보 추출
        Long userId = userDetails.getUserId();

        // 페이징 객체 생성 (최신순 정렬: 생성일 내림차순)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // 여행 보드 목록 조회
        TripBoardPageResponse response = tripBoardService.getTripBoards(userId, pageable);

        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }
}