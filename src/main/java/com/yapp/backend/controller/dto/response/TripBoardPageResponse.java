package com.yapp.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 여행 보드 목록 조회 API의 무한 스크롤 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardPageResponse {
    private List<TripBoardSummaryResponse> tripBoards;
    private boolean hasNext;
    private long totalCnt;
}