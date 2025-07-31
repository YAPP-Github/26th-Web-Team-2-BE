package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class AddAccommodationRequest {
    @NotNull(message = "비교표에 추가할 숙소 ID 리스트를 입력해주세요.")
    private List<Long> accommodationIds;
}
