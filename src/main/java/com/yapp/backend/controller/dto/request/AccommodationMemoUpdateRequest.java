package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AccommodationMemoUpdateRequest {

    @Size(max = 100, message = "메모는 100자 이내로 입력해주세요.")
    private String memo;
}