package com.yapp.backend.controller.dto.response;

import com.yapp.backend.repository.enums.TripBoardRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private TripBoardRole userRole;
    private int participantCount;
    private List<ParticipantProfileResponse> participants;
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