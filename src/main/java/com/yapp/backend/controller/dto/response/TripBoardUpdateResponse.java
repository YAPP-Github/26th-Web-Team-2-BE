package com.yapp.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardUpdateResponse {
    private Long tripBoardId;
    private String boardName;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime updatedAt;
}