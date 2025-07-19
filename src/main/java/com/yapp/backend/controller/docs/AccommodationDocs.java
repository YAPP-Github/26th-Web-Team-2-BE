package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.request.AccommodationSearchRequest;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

@Tag(name = "숙소 API", description = "숙소 관련 API")
public interface AccommodationDocs {
	@Operation(summary = "숙소 목록 조회", description = "제목에 포함된 키워드를 통해 숙소 목록을 검색합니다.")
	@ApiResponse(responseCode = "200", description = "숙소 목록 조회 성공", content = @Content(schema = @Schema(implementation = StandardResponse.class)))
	ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationsByTitle(
		@Parameter(in = ParameterIn.QUERY, schema = @Schema(implementation = AccommodationSearchRequest.class)) AccommodationSearchRequest request);
}
