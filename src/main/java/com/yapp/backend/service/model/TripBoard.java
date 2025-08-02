package com.yapp.backend.service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoard {
    private Long id;
    private String boardName;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private User createdBy;
    private User updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UserTripBoard> participants;

    /**
     * 여행 기간을 "yy.MM.dd~yy.MM.dd" 형식으로 포맷팅하여 반환
     */
    public String getFormattedTravelPeriod() {
        if (startDate == null || endDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        return startDate.format(formatter) + "~" + endDate.format(formatter);
    }
}
