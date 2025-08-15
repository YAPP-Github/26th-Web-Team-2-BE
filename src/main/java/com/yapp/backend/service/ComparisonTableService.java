package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.service.model.ComparisonTable;

public interface ComparisonTableService {
    Long createComparisonTable(CreateComparisonTableRequest request, Long userId);

    ComparisonTable getComparisonTable(Long tableId);

    Boolean updateComparisonTable(Long tableId, UpdateComparisonTableRequest request, Long userId);

    ComparisonTable addAccommodationToComparisonTable(Long tableId, AddAccommodationRequest request, Long userId);

    void deleteComparisonTable(Long tableId, Long userId);
}
