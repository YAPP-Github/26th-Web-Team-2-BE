package com.yapp.backend.service;

import com.yapp.backend.controller.dto.response.AccommodationPageResponse;

public interface AccommodationService {
    AccommodationPageResponse findAccommodationsByTitle(String title, int page, int size);
}
