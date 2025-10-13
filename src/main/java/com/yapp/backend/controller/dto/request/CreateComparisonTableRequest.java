package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateComparisonTableRequest {
    @NotNull(message = "여행 보드 ID는 필수 입력값입니다.")
    private Long tripBoardId;

    // tableName이 null이거나 빈 문자열이면 자동 생성됩니다
    private String tableName;

    @NotEmpty(message = "숙소 정렬 순서대로 ID를 입력해주세요. 1개 이상 필수 입력입니다.")
    @Size(min = 1, max = 10, message = "비교표에는 최소 1개, 최대 10개의 숙소를 추가할 수 있습니다.")
    private List<Long> accommodationIdList;

    // factorList가 비어있거나 null이면 모든 비교 요소가 디폴트 순서로 자동 설정됩니다
    private List<String> factorList;

}
