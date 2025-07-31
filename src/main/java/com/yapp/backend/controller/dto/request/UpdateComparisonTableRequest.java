package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;

@Getter
public class UpdateComparisonTableRequest {
    @NotNull(message = "여행 보드 ID는 필수 입력값입니다.")
    private Long boardId;

    @NotBlank
    private String tableName;

    @NotNull(message = "수정될 숙소 상세 정보 객체 리스트입니다.")
    private List<UpdateAccommodationRequest> accommodationRequestList;

    @NotEmpty(message = "숙소 정렬 순서대로 ID를 입력해주세요. 1개 이상 필수 입력입니다.")
    private List<Long> accommodationIdList;

    @NotEmpty(message = "비교표에 표시할 비교기준항목을 정렬 순서대로 입력해주세요. 1개 이상 필수 입력입니다.")
    private List<String> factorList;

}
