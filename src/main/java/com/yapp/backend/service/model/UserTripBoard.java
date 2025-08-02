package com.yapp.backend.service.model;

import com.yapp.backend.repository.enums.TripBoardRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTripBoard {
    private Long id;
    private User user;
    private TripBoard tripBoard;
    private String invitationUrl;
    private Boolean invitationActive;
    private TripBoardRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}