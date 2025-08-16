package com.yapp.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 비교표 목록 조회 API의 무한 스크롤 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonTablePageResponse {
    private List<ComparisonTableSummaryResponse> comparisonTables;
    private boolean hasNext;
    
    /**
     * 정적 팩토리 메서드
     * @param comparisonTables 비교표 요약 리스트
     * @param hasNext 다음 페이지 존재 여부
     * @return ComparisonTablePageResponse 객체
     */
    public static ComparisonTablePageResponse of(List<ComparisonTableSummaryResponse> comparisonTables, boolean hasNext) {
        return ComparisonTablePageResponse.builder()
                .comparisonTables(comparisonTables)
                .hasNext(hasNext)
                .build();
    }
}
