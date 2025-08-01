package com.yapp.backend.service.model;

import com.yapp.backend.service.model.enums.ComparisonFactor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ComparisonTable from(
            @NotBlank String tableName,
            User user,
            TripBoard tripBoard,
            List<Accommodation> accommodationList,
            List<ComparisonFactor> factors
    ) {
        return ComparisonTable.builder()
                .tableName(tableName)
                .createdById(user.getId())
                .tripBoardId(tripBoard.getId())
                .accommodationList(accommodationList)
                .factors(factors)
                .build();
    }

}
