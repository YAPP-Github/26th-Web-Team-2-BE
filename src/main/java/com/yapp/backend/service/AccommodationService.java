package com.yapp.backend.service;

import com.yapp.backend.controller.dto.response.AccommodationPageResponse;

public interface AccommodationService {
    AccommodationPageResponse findAccommodationsByTableId(Integer tableId, int page, int size, Long userId);
}
