package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationCountResponse;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;

import com.yapp.backend.filter.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "숙소 API", description = "숙소 관련 API")
public interface AccommodationDocs {
	@Operation(summary = "숙소 목록 조회", description = "숙소 목록을 조회합니다.")
	@SecurityRequirement(name = "JWT")
	ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationByBoardIdAndUserId(
			@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "숙소가 포함된 여행보드의 ID") @NotNull(message = "여행보드 ID는 필수입니다.") @Min(value = 1, message = "여행보드 ID는 1 이상이어야 합니다.") Long boardId,
			@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 번호") @NotNull(message = "페이지 번호는 필수입니다.") @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.") Integer page,
			@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 크기") @NotNull(message = "페이지 크기는 필수입니다.") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") Integer size,
			@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer", format = "int64"), description = "유저 ID, 없는 경우 모든 유저가 생성한 숙소 목록을 반환합니다. 현재 parameter로 받는 것은 임시 로직입니다.") Long userId,
			@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "string"), description = "정렬 기준, 기본 값은 saved_at_desc(최근 등록순)이고, price_asc(최저 가격순)을 제공합니다.") String sort,
			@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

	@Operation(summary = "여행보드 숙소 개수 조회", description = "여행보드에 포함된 숙소의 개수를 조회합니다.")
	@SecurityRequirement(name = "JWT")
	ResponseEntity<StandardResponse<AccommodationCountResponse>> getAccommodationCountByBoardId(
			@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer", format = "int64"), description = "숙소가 포함된 여행보드의 ID") @Min(value = 1, message = "여행보드 ID는 1 이상이어야 합니다.") Long boardId,
			@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer", format = "int64"), description = "유저 ID, 없는 경우 모든 유저가 생성한 숙소 목록을 반환합니다. 현재 parameter로 받는 것은 임시 로직입니다.") Long userId,
			@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

	@Operation(summary = "숙소 카드 등록", description = "링크를 첨부하여 숙소 카드를 등록합니다.", method = "POST")
	ResponseEntity<StandardResponse<AccommodationRegisterResponse>> registerAccommodationCard(
			@RequestBody(description = "숙소 등록 요청 데이터", content = @Content(schema = @Schema(implementation = AccommodationRegisterRequest.class))) @Valid AccommodationRegisterRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

	@Operation(summary = "숙소 단건 조회", description = "특정 숙소 ID로 숙소 정보를 조회합니다.")
	ResponseEntity<StandardResponse<AccommodationResponse>> getAccommodationById(
			@Parameter(in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64"), description = "조회할 숙소의 ID") @NotNull(message = "숙소 ID는 필수입니다.") @Min(value = 1, message = "숙소 ID는 1 이상이어야 합니다.") Long accommodationId,
			@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);
}
