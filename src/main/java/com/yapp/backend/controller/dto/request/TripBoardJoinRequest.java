package com.yapp.backend.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "여행 보드 참여 요청")
public class TripBoardJoinRequest {

    @NotBlank(message = "초대링크는 필수입니다.")
    @Schema(description = "여행 보드 초대 코드", example = "abc123def456", required = true)
    private String invitationUrl;
}