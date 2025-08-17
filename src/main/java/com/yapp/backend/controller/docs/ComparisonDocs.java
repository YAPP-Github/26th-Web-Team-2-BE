package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.AmenityFactorList;
import com.yapp.backend.controller.dto.response.ComparisonFactorList;
import com.yapp.backend.controller.dto.response.ComparisonTableDeleteResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.controller.dto.response.ComparisonTablePageResponse;
import com.yapp.backend.controller.dto.response.CreateComparisonTableResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "비교표 API", description = "비교표 관련 API")
public interface ComparisonDocs {

    @Operation(summary = "비교표 기준 항목 Enum 리스트", description = "비교 기준 항목 Enum 리스트를 반환합니다. (인증 불필요)")
    ResponseEntity<StandardResponse<ComparisonFactorList>> getComparisonFactorList();

    @Operation(summary = "편의 서비스 Enum 리스트", description = "편의 서비스 항목 Enum 리스트를 반환합니다. (인증 불필요)")
    ResponseEntity<StandardResponse<AmenityFactorList>> getAmenityFactorList();

    @Operation(
            summary = "비교표 생성",
            description = "비교표 이름, 숙소 ID 리스트, 비교 기준 항목을 받아서 비교표 메타 데이터를 생성합니다. "
                    + "(Authorization 헤더에 Bearer 토큰 필요)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비교표 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰 또는 인증 정보 없음"),
            @ApiResponse(responseCode = "403", description = "비교표 생성 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "비교표, 숙소 등 관련 리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 비교표 생성 중 오류 발생")
    })
    ResponseEntity<StandardResponse<CreateComparisonTableResponse>> createComparisonTable(
            @RequestBody CreateComparisonTableRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
        summary = "비교표 조회", 
        description = "비교표 메타 데이터와 포함된 숙소 정보 리스트를 조회합니다. JWT 인증이 필요하며, 비교표 생성자 또는 여행보드 참여자만 접근 가능합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비교표 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰 또는 인증 정보 없음"),
        @ApiResponse(responseCode = "403", description = "비교표 조회 권한이 없음"),
        @ApiResponse(responseCode = "404", description = "비교표, 숙소 등 관련 리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 비교표 조회 중 오류 발생")
    })
    ResponseEntity<StandardResponse<ComparisonTableResponse>> getComparisonTable(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "숙소가 포함된 테이블의 ID") Long tableId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
        summary = "shareCode를 통한 비교표 조회", 
        description = "shareCode를 사용하여 인증 없이 비교표를 조회합니다. 유효한 shareCode가 필요합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비교표 조회 성공"),
        @ApiResponse(responseCode = "403", description = "공유 코드가 유효하지 않음"),
        @ApiResponse(responseCode = "404", description = "비교표, 숙소 등 관련 리소스를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 비교표 조회 중 오류 발생")
    })
    ResponseEntity<StandardResponse<ComparisonTableResponse>> getComparisonTableByShareCode(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "숙소가 포함된 테이블의 ID") Long tableId,
            @Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "string"), description = "비교표 공유 코드", required = true) String shareCode
    );


    @Operation(summary = "비교표 수정", description = "비교표 메타 데이터(제목)와 숙소 세부 내용, 비교 기준 정렬 순서, 숙소 정렬 순서를 수정합니다. (Authorization 헤더에 Bearer 토큰 필요)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비교표 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰 또는 인증 정보 없음"),
            @ApiResponse(responseCode = "403", description = "비교표 수정 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "비교표, 숙소, 여행 보드 등 관련 리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 비교표 수정 중 오류 발생")
    })
    ResponseEntity<StandardResponse<Boolean>> updateComparisonTable(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "수정할 테이블의 ID") Long tableId,
            @RequestBody UpdateComparisonTableRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "비교표 숙소 추가", description = "비교표에 새로운 숙소를 추가합니다. (Authorization 헤더에 Bearer 토큰 필요)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비교표에 숙소 추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰 또는 인증 정보 없음"),
            @ApiResponse(responseCode = "403", description = "비교표 수정 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "비교표, 숙소 등 관련 리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 비교표 수정 중 오류 발생")
    })
    ResponseEntity<StandardResponse<ComparisonTableResponse>> addAccommodationToComparisonTable(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "테이블의 ID") Long tableId,
            @RequestBody AddAccommodationRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "비교표 삭제", description = "사용자가 생성한 비교표를 삭제합니다. 생성자만 삭제할 수 있습니다. (Authorization 헤더에 Bearer 토큰 필요)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비교표 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰 또는 인증 정보 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 비교표 생성자가 아님"),
            @ApiResponse(responseCode = "404", description = "비교표를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 비교표 삭제 중 오류 발생")
    })
    ResponseEntity<StandardResponse<ComparisonTableDeleteResponse>> deleteComparisonTable(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "삭제할 비교표의 ID") Long tableId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(
            summary = "여행보드의 비교표 리스트 조회",
            description = "특정 여행보드에 생성된 모든 비교표의 리스트를 무한스크롤 페이지네이션으로 조회합니다. " +
                         "각 비교표의 기본 정보와 포함된 숙소 정보를 제공합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비교표 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰 또는 인증 정보 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 여행보드 멤버가 아님"),
            @ApiResponse(responseCode = "404", description = "여행보드 등 관련 리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 - 비교표 리스트 조회 중 오류 발생")
    })
    ResponseEntity<StandardResponse<ComparisonTablePageResponse>> getComparisonTablesByTripBoard(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "조회할 여행보드의 ID") Long tripBoardId,
            @Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 번호 (0부터 시작)") Integer page,
            @Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 크기") Integer size,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

}
