package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "비교표 목록 조회 응답")
public class ComparisonTableSummaryResponse {

    @Schema(description = "비교표 ID", example = "1")
    private Long tableId;

    @Schema(description = "비교표 이름", example = "제주도 숙소 비교")
    private String tableName;

    @Schema(description = "포함된 숙소 개수", example = "3")
    private Integer accommodationCount;

    @Schema(description = "포함된 숙소 이름들", example = "[\"호텔 신라\", \"롯데 호텔\", \"그랜드 하이얏트\"]")
    private List<String> accommodationNames;

    @Schema(description = "최근 수정일", example = "2025-08-15T10:30:00")
    private LocalDateTime lastModifiedAt;

    @Schema(description = "공유 코드", accessMode = Schema.AccessMode.READ_ONLY, example = "4473fa9aed7044c7a94fa6e99..")
    private String shareCode;
}
