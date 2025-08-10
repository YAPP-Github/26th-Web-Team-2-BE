package com.yapp.backend.controller.dto.response;

import com.yapp.backend.common.util.DateUtil;
import com.yapp.backend.service.model.TripBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "여행 보드 참여 응답")
public class TripBoardJoinResponse {

    @Schema(description = "여행 보드 ID", example = "1")
    private Long tripBoardId;

    @Schema(description = "여행 보드 이름", example = "제주도 여행")
    private String boardName;

    @Schema(description = "여행 목적지", example = "제주도")
    private String destination;

    @Schema(description = "여행 기간", example = "25.08.14~08.16")
    private String travelPeriod;

    @Schema(description = "현재 참여자 수", example = "3")
    private Integer participantCount;

    @Schema(description = "보드 참여 시간", example = "2025-01-09T10:30:00")
    private LocalDateTime joinedAt;

    /**
     * TripBoard 도메인 모델로부터 TripBoardJoinResponse를 생성하는 정적 팩토리 메서드
     */
    public static TripBoardJoinResponse from(TripBoard tripBoard, Integer participantCount, LocalDateTime joinedAt) {
        return TripBoardJoinResponse.builder()
                .tripBoardId(tripBoard.getId())
                .boardName(tripBoard.getBoardName())
                .destination(tripBoard.getDestination())
                .travelPeriod(DateUtil.formatTravelPeriod(tripBoard.getStartDate(), tripBoard.getEndDate()))
                .participantCount(participantCount)
                .joinedAt(joinedAt)
                .build();
    }
}