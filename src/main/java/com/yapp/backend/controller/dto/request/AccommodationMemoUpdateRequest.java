package com.yapp.backend.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "숙소 메모 수정 요청")
public class AccommodationMemoUpdateRequest {

    @Schema(description = "수정할 메모 내용", example = "이 숙소는 바다 전망이 좋고 조식이 맛있어요!", maxLength = 50, nullable = true)
    @Size(max = 50, message = "메모는 50자 이내로 입력해주세요.")
    private String memo;
}