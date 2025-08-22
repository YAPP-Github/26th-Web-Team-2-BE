package com.yapp.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "숙소 메모 수정 응답")
public class AccommodationMemoUpdateResponse {

    @Schema(description = "숙소 ID", example = "1")
    private Long accommodationId;

    @Schema(description = "수정된 메모 내용", example = "이 숙소는 바다 전망이 좋고 조식이 맛있어요!", nullable = true)
    private String memo;

    @Schema(description = "메모 수정 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    public static AccommodationMemoUpdateResponse of(Long accommodationId, String memo, LocalDateTime updatedAt) {
        return AccommodationMemoUpdateResponse.builder()
                .accommodationId(accommodationId)
                .memo(memo)
                .updatedAt(updatedAt)
                .build();
    }
}