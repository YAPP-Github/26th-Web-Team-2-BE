package com.yapp.backend.controller.dto.request.update;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AmenityUpdate {
    @NotNull(message = "비교 기준 enum 값을 넣어주세요.")
    private String type;
    private boolean available;
    private String description;
}
