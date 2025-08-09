package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.TripBoardCreateRequest;
import com.yapp.backend.controller.dto.request.TripBoardUpdateRequest;
import com.yapp.backend.controller.dto.response.TripBoardCreateResponse;
import com.yapp.backend.controller.dto.response.TripBoardDeleteResponse;
import com.yapp.backend.controller.dto.response.TripBoardLeaveResponse;
import com.yapp.backend.controller.dto.response.TripBoardPageResponse;
import com.yapp.backend.controller.dto.response.TripBoardUpdateResponse;
import org.springframework.data.domain.Pageable;

public interface TripBoardService {
    TripBoardCreateResponse createTripBoard(TripBoardCreateRequest request, Long userId);

    TripBoardPageResponse getTripBoards(Long userId, Pageable pageable);

    TripBoardUpdateResponse updateTripBoard(Long tripBoardId, TripBoardUpdateRequest request, Long userId);

    TripBoardLeaveResponse leaveTripBoard(Long tripBoardId, Long userId, Boolean removeResources);

    TripBoardDeleteResponse deleteTripBoard(Long tripBoardId, Long userId);
}