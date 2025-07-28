package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;

public interface ComparisonTableService {
    Long createComparisonTable(CreateComparisonTableRequest request, Long userId);
    ComparisonTableResponse getComparisonTable(Long tableId, Long userId);
}
