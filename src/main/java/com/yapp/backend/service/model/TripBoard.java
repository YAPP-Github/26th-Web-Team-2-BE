package com.yapp.backend.service.model;

import com.yapp.backend.common.util.DateUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
}
