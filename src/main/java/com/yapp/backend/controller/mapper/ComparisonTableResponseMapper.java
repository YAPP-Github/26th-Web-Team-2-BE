package com.yapp.backend.controller.mapper;

import com.yapp.backend.controller.dto.response.ComparisonTableSummaryResponse;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ComparisonTable 도메인 모델을 Response DTO로 변환하는 매퍼 클래스
 * Controller 레이어에서 Service로부터 받은 도메인 모델을 응답용 DTO로 변환합니다.
 */
@Component
public class ComparisonTableResponseMapper {

    /**
     * ComparisonTable 도메인 모델을 ComparisonTableListResponse DTO로 변환
     * 
     * @param comparisonTable 비교표 도메인 모델
     * @return 비교표 리스트 응답 DTO
     */
    public ComparisonTableSummaryResponse toSummaryResponse(ComparisonTable comparisonTable) {
        List<String> accommodationNames = comparisonTable.getAccommodationList().stream()
                .map(Accommodation::getAccommodationName)
                .collect(Collectors.toList());

        return new ComparisonTableSummaryResponse(
                comparisonTable.getId(),
                comparisonTable.getTableName(),
                comparisonTable.getAccommodationList().size(),
                accommodationNames,
                comparisonTable.getUpdatedAt(),
                comparisonTable.getShareCode()
        );
    }

    /**
     * ComparisonTable 도메인 모델 리스트를 ComparisonTableListResponse DTO 리스트로 변환
     * 
     * @param comparisonTables 비교표 도메인 모델 리스트
     * @return 비교표 리스트 응답 DTO 리스트
     */
    public List<ComparisonTableSummaryResponse> toResponseList(List<ComparisonTable> comparisonTables) {
        return comparisonTables.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }
}
