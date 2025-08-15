package com.yapp.backend.controller.mapper;

import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.service.model.ComparisonTable;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 비교표 도메인과 DTO 간의 변환을 담당하는 Mapper
 */
@Component
public class ComparisonTableResponseMapper {

    /**
     * ComparisonTable 도메인을 ComparisonTableResponse DTO로 변환
     */
    public ComparisonTableResponse toResponse(ComparisonTable comparisonTable) {
        return new ComparisonTableResponse(
                comparisonTable.getId(),
                comparisonTable.getTableName(),
                comparisonTable.getAccommodationList().stream()
                        .map(AccommodationResponse::from)
                        .collect(Collectors.toList()),
                comparisonTable.getShareCode(),
                comparisonTable.getFactors(),
                comparisonTable.getCreatedById()
        );
    }
}
