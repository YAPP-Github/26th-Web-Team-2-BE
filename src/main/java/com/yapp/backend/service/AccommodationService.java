package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;

import com.yapp.backend.controller.dto.response.AccommodationResponse;

public interface AccommodationService {
    AccommodationPageResponse findAccommodationsByTableId(Long tableId, int page, int size, Long userId, String sort);

    Long countAccommodationsByTableId(Long tableId, Long userId);

    AccommodationRegisterResponse registerAccommodationCard(AccommodationRegisterRequest request);

    AccommodationResponse findAccommodationById(Long accommodationId);
}
