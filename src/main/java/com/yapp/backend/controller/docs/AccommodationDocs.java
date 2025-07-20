package com.yapp.backend.controller.docs;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

@Tag(name = "숙소 API", description = "숙소 관련 API")
public interface AccommodationDocs {
	@Operation(summary = "숙소 목록 조회", description = "숙소 목록을 조회합니다.")
	ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationByTableIdAndUserId(
		@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "숙소가 포함된 테이블의 ID") Integer tableId,
		@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 번호") Integer page,
		@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer"), description = "페이지 크기") Integer size,
		@Parameter(in = ParameterIn.QUERY, schema = @Schema(type = "integer", format = "int64"), description = "유저 ID, 없는 경우 모든 유저가 생성한 숙소 목록을 반환합니다.") Long userId
	);
}
