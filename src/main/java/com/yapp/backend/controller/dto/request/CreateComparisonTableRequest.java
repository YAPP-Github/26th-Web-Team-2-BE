package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateComparisonTableRequest {
    @NotNull
    private Long groupId;

    @NotBlank
    private String tableName;

    @NotEmpty
    private List<Long> accommodationIdList;

    @NotEmpty
    private List<String> factorList;

}
