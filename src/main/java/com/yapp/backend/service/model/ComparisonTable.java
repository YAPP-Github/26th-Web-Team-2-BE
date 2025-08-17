package com.yapp.backend.service.model;

import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.InvalidComparisonTable;
import com.yapp.backend.common.util.ShareCodeGeneratorUtil;
import com.yapp.backend.service.model.enums.ComparisonFactor;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonTable {
    private Long id;
    private String tableName;
    private Long createdById;
    private Long tripBoardId;
    private List<Accommodation> accommodationList;
    private List<ComparisonFactor> factors;
    private String shareCode;
    private Instant createdAt;
    private Instant updatedAt;

    public static ComparisonTable from(
            @NotBlank String tableName,
            User user,
            TripBoard tripBoard,
            List<Accommodation> accommodationList,
            List<ComparisonFactor> factors
    ) {
        ComparisonTable newTable = ComparisonTable.builder()
                .tableName(tableName)
                .createdById(user.getId())
                .tripBoardId(tripBoard.getId())
                .accommodationList(accommodationList)
                .factors(factors)
                .shareCode(ShareCodeGeneratorUtil.generateUniqueShareCode())
                .build();
        newTable.createdAt = Instant.now();
        newTable.updatedAt = Instant.now();
        return newTable;
    }

    /**
     * 비교 테이블의 모든 정보를 업데이트합니다.
     * shareCode는 보존됩니다.
     * @param tableName 새로운 테이블명
     * @param accommodationList 새로운 숙소 리스트
     * @param factors 새로운 비교 기준
     */
    public void updateTable(String tableName, List<Accommodation> accommodationList, List<ComparisonFactor> factors) {
        this.tableName = tableName;
        this.accommodationList = accommodationList;
        this.factors = factors;
        this.updatedAt = Instant.now();
        // shareCode는 보존 (생성 시에만 설정)
    }
    /**
     * 도메인 객체를 Entity로 변환하기 전에 유효성을 검증합니다.
     * @throws InvalidComparisonTable 유효하지 않은 경우
     */
    public void validateBeforeSave() {
        if (!isValid()) {
            throw new InvalidComparisonTable(ErrorCode.INVALID_COMPARISON_TABLE);
        }
    }


    /**
     * 비교 테이블의 유효성을 검증합니다.
     * @return 유효성 검증 결과
     */
    private boolean isValid() {
        return tableName != null && !tableName.trim().isEmpty()
                && accommodationList != null && !accommodationList.isEmpty()
                && shareCode != null;
    }

}
