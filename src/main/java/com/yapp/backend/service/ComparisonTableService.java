package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;

public interface ComparisonTableService {
    Long createComparisonTable(CreateComparisonTableRequest request, Long userId);

    ComparisonTableResponse getComparisonTable(Long tableId, Long userId);

    Boolean updateComparisonTable(Long tableId, UpdateComparisonTableRequest request, Long userId);

    ComparisonTableResponse addAccommodationToComparisonTable(Long tableId, AddAccommodationRequest request, Long userId);

    void deleteComparisonTable(Long tableId, Long userId);
}
