package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;

public interface TripBoardService {
    TripBoardCreateResponse createTripBoard(TripBoardCreateRequest request, Long userId);
}