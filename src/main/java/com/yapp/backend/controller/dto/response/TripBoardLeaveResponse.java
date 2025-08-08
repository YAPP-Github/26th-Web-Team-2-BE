package com.yapp.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 여행보드 나가기 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardLeaveResponse {

    private Long tripBoardId;
    private LocalDateTime leftAt;
}