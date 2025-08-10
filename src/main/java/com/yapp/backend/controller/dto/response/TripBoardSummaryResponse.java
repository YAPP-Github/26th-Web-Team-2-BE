package com.yapp.backend.controller.dto.response;

import com.yapp.backend.repository.enums.TripBoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 여행 보드 요약 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardSummaryResponse {
    private Long boardId;
    private String boardName;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String travelPeriod; // "25.08.14~08.16" 형식
    private TripBoardRole userRole;
    private int participantCount;
    private int accommodationCount; // 숙소 개수
    private List<ParticipantProfileResponse> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}