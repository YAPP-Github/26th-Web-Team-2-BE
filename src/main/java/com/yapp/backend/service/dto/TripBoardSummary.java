package com.yapp.backend.service.dto;

import com.yapp.backend.repository.enums.TripBoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 여행 보드 요약 정보를 담는 DTO
 * Repository 계층에서 조회한 데이터를 Service 계층으로 전달하기 위해 사용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardSummary {
    private Long boardId;
    private String boardName;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private TripBoardRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 여행 기간을 "yy.MM.dd~yy.MM.dd" 형식으로 포맷팅하여 반환
     * 
     * @return 포맷팅된 여행 기간 문자열 (예: "25.08.14~25.08.16")
     */
    public String getTravelPeriod() {
        if (startDate == null || endDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        return startDate.format(formatter) + "~" + endDate.format(formatter);
    }
}