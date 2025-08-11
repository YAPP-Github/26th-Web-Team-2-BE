package com.yapp.backend.service.dto;

import com.yapp.backend.repository.enums.TripBoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 여행 보드 요약 정보를 담는 DTO
 * Repository 계층에서 조회한 데이터를 Service 계층으로 전달하기 위해 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardSummary {
    private Long tripBoardId;
    private String boardName;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String travelPeriod; // "25.08.14~08.16" 형식
    private TripBoardRole userRole;
    private int accommodationCount; // 숙소 개수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}