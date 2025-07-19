package com.yapp.backend.controller.dto.response;

import com.yapp.backend.domain.Accommodation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationPageResponse {
    private List<AccommodationResponse> accommodations;
    private boolean hasNext;
}
