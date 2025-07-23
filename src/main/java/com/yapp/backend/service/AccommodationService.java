package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;

public interface AccommodationService {
    AccommodationPageResponse findAccommodationsByTableId(Integer tableId, int page, int size, Long userId);

    Long countAccommodationsByTableId(Long tableId, Long userId);

	AccommodationRegisterResponse registerAccommodationCard(AccommodationRegisterRequest request);
}
