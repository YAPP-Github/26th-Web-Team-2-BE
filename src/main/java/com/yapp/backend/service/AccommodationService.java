package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.request.UpdateAccommodationRequest;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;

import com.yapp.backend.controller.dto.response.AccommodationResponse;

public interface AccommodationService {
    AccommodationPageResponse findAccommodationsByTripBoardId(Long tripBoardId, int page, int size, Long userId, String sort);

    Long countAccommodationsByTripBoardId(Long tripBoardId, Long userId);

    AccommodationRegisterResponse registerAccommodationCard(AccommodationRegisterRequest request, Long userId);

    AccommodationResponse findAccommodationById(Long accommodationId);

    void updateAccommodation(UpdateAccommodationRequest request);
}
