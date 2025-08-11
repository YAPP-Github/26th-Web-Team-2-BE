package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateComparisonTableRequest {
    @NotNull(message = "여행 보드 ID는 필수 입력값입니다.")
    private Long tripBoardId;

    @NotBlank
    private String tableName;

    @NotEmpty(message = "숙소 정렬 순서대로 ID를 입력해주세요. 1개 이상 필수 입력입니다.")
    private List<Long> accommodationIdList;

    // factorList가 비어있거나 null이면 모든 비교 요소가 디폴트 순서로 자동 설정됩니다
    private List<String> factorList;

}
