package com.yapp.backend.controller.docs;


import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonFactorList;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.controller.dto.response.CreateComparisonTableResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "비교표 API", description = "비교표 관련 API")
public interface ComparisonDocs {

    @Operation(summary = "비교표 기준 항목 Enum 리스트", description = "비교 기준 항목 Enum 리스트를 반환합니다.")
    ResponseEntity<StandardResponse<ComparisonFactorList>> getComparisonFactorList();

    @Operation(summary = "비교표 생성", description = "비교표 이름, 숙소 ID 리스트, 비교 기준 항목을 받아서 비교표 메타 데이터를 생성합니다.")
    ResponseEntity<StandardResponse<CreateComparisonTableResponse>> createComparisonTable(
            @RequestBody CreateComparisonTableRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "비교표 조회", description = "비교표 메타 데이터와 포함된 숙소 정보 리스트를 조회합니다.")
    ResponseEntity<StandardResponse<ComparisonTableResponse>> getComparisonTable(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "숙소가 포함된 테이블의 ID") Long tableId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "비교표 수정", description = "비교표 메타 데이터와 비교 기준 항목을 수정합니다.")
    ResponseEntity<StandardResponse<ComparisonTableResponse>> updateComparisonTable(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "수정할 테이블의 ID") Long tableId,
            @RequestBody CreateComparisonTableRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "비교표 숙소 추가", description = "비교표에 새로운 숙소를 추가합니다.")
    ResponseEntity<StandardResponse<ComparisonTableResponse>> addAccommodationToComparisonTable(
            @Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer"), description = "테이블의 ID") Long tableId,
            @Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "숙소 ID") Long accommodationId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

}
