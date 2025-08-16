package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.response.UserInfoResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "USER API", description = "사용자 관련 API")
public interface UserDocs {

    @Operation(
            summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 기본 정보(닉네임, 프로필 이미지)를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "사용자 정보 조회 성공"
    )
    @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자"
    )
    @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음 또는 탈퇴한 사용자"
    )
    @SecurityRequirement(name = "JWT")
    ResponseEntity<StandardResponse<UserInfoResponse>> getUserInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );
}