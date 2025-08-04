package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.controller.dto.response.TripBoardPageResponse;
import org.springframework.data.domain.Pageable;

public interface TripBoardService {
    TripBoardCreateResponse createTripBoard(TripBoardCreateRequest request, Long userId);

    TripBoardPageResponse getTripBoards(Long userId, Pageable pageable);
}