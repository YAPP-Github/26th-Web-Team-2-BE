package com.yapp.backend.controller.dto.response;

import com.yapp.backend.service.model.enums.ComparisonFactor;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ComparisonTableResponse {
    private Long tableId;
    private String tableName;
    private List<AccommodationResponse> accommodationResponsesList;
    private String shareCode;
    private List<ComparisonFactor> factorsList;
    private Long createdBy;
    private String creatorName;
}
