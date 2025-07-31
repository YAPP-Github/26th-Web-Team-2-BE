package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "여행 보드 API", description = "여행 보드 관련 API")
public interface TripBoardDocs {

        @Operation(summary = "여행 보드 생성", description = "새로운 여행 보드를 생성합니다. JWT 인증을 통해 현재 사용자 정보를 추출하고, 생성자는 자동으로 OWNER 역할로 등록되며 고유한 초대 링크가 생성됩니다.")
        ResponseEntity<StandardResponse<TripBoardCreateResponse>> createTripBoard(
                        @RequestBody TripBoardCreateRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);
}