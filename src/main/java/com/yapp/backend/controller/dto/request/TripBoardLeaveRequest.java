package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 여행보드 나가기 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardLeaveRequest {

    @NotNull(message = "리소스 제거 여부를 선택해주세요")
    private Boolean removeResources;
}