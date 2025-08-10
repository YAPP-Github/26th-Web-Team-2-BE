package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.request.TripBoardJoinRequest;
import com.yapp.backend.controller.dto.request.TripBoardLeaveRequest;
import com.yapp.backend.controller.dto.request.TripBoardUpdateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.controller.dto.response.TripBoardJoinResponse;
import com.yapp.backend.controller.dto.response.TripBoardLeaveResponse;
import com.yapp.backend.controller.dto.response.TripBoardDeleteResponse;
import com.yapp.backend.controller.dto.response.TripBoardPageResponse;
import com.yapp.backend.controller.dto.response.TripBoardUpdateResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "여행 보드 API", description = "여행 보드 관련 API")
public interface TripBoardDocs {

        @Operation(summary = "여행 보드 생성", description = "새로운 여행 보드를 생성합니다. JWT 인증을 통해 현재 사용자 정보를 추출하고, 생성자는 자동으로 OWNER 역할로 등록되며 고유한 초대 코드가 생성됩니다.")
        @SecurityRequirement(name = "JWT")
        ResponseEntity<StandardResponse<TripBoardCreateResponse>> createTripBoard(
                        @RequestBody @Valid TripBoardCreateRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

        @Operation(summary = "여행 보드 목록 조회", description = "사용자가 참여한 여행 보드 목록을 페이징으로 조회합니다. JWT 인증을 통해 현재 사용자 정보를 추출하고, 최신순으로 정렬된 결과를 반환합니다.")
        @SecurityRequirement(name = "JWT")
        ResponseEntity<StandardResponse<TripBoardPageResponse>> getTripBoards(
                        @Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 번호") @NotNull(message = "페이지 번호는 필수입니다.") @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.") Integer page,
                        @Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 크기") @NotNull(message = "페이지 크기는 필수입니다.") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") Integer size,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

        @Operation(summary = "여행 보드 참여", description = "초대 코드를 통해 기존 여행 보드에 참여합니다. JWT 인증을 통해 현재 사용자 정보를 추출하고, 초대 코드의 유효성을 검증한 후 보드에 참여자로 등록합니다.")
        @SecurityRequirement(name = "JWT")
        ResponseEntity<StandardResponse<TripBoardJoinResponse>> joinTripBoard(
                        @RequestBody @Valid TripBoardJoinRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

        @Operation(summary = "여행 보드 수정", description = "기존 여행 보드의 기본 정보(보드 이름, 목적지, 여행 기간)를 수정합니다. JWT 인증을 통해 현재 사용자 정보를 추출하고, 수정된 보드 정보를 반환합니다.")
        @SecurityRequirement(name = "JWT")
        ResponseEntity<StandardResponse<TripBoardUpdateResponse>> updateTripBoard(
                        @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "여행 보드 ID") @PathVariable Long tripBoardId,
                        @RequestBody @Valid TripBoardUpdateRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

        @Operation(summary = "여행 보드 삭제", description = "여행 보드와 관련된 모든 데이터를 삭제합니다. 오직 여행 보드의 소유자(OWNER)만이 삭제할 수 있으며, 삭제 시 해당 보드에 연관된 모든 리소스(숙소 정보, 멤버 매핑 관계, 비교표 등)가 함께 제거됩니다.")
        @SecurityRequirement(name = "JWT")
        ResponseEntity<StandardResponse<TripBoardDeleteResponse>> deleteTripBoard(
                        @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "여행 보드 ID") @PathVariable Long tripBoardId,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

        @Operation(summary = "여행 보드 나가기", description = "여행 보드에서 나갑니다. OWNER인 경우 가장 먼저 입장한 MEMBER에게 권한이 이양되며, 마지막 참여자인 경우 여행보드가 삭제됩니다. 나가는 사용자는 자신이 생성한 리소스(비교표, 숙소)를 유지하거나 제거할 수 있습니다.")
        @SecurityRequirement(name = "JWT")
        ResponseEntity<StandardResponse<TripBoardLeaveResponse>> leaveTripBoard(
                        @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "여행 보드 ID") @PathVariable Long tripBoardId,
                        @RequestBody @Valid TripBoardLeaveRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);
}