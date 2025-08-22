package com.yapp.backend.controller.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationMemoUpdateResponse {

    private Long accommodationId;
    private String memo;
    private LocalDateTime updatedAt;

    public static AccommodationMemoUpdateResponse of(Long accommodationId, String memo, LocalDateTime updatedAt) {
        return AccommodationMemoUpdateResponse.builder()
                .accommodationId(accommodationId)
                .memo(memo)
                .updatedAt(updatedAt)
                .build();
    }
}