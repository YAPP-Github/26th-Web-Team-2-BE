package com.yapp.backend.service.model.enums;

import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.InValidFactorsException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ComparisonFactor {
    PARKING,                // 주차 가능 여부
    BREAKFAST,              // 조식 가능 여부
    FREE_WIFI,              // 무료 와이파이 여부
    POOL,                   // 수영장 보유 여부
    FITNESS,                // 피트니스, 헬스장 보유 여부
    LUGGAGE_STORAGE,        // 짐 보관 가능 여부
    BAR_LOUNGE,             // 바/라운지 보유 여부
    FRONT_DESK_HOURS,       // 프론트데스크 운영시간
    PET_FRIENDLY,           // 반려동물 동반 가능 여부
    BUSINESS_SERVICES,      // 비즈니스 서비스
    CLEANING_SERVICE,       // 청소 서비스
    HANDICAP_FACILITIES;    // 장애인 편의시설

    /**
     * 문자열 리스트를 ComparisonFactor 리스트로 변환
     */
    public static List<ComparisonFactor> convertToComparisonFactorList(List<String> factorNames) {
        return factorNames.stream()
                .map(name -> Arrays.stream(ComparisonFactor.values())
                        .filter(f -> f.name().equals(name))
                        .findFirst()
                        .orElseThrow(() -> new InValidFactorsException(ErrorCode.INVALID_FACTORS))
                )
                .collect(Collectors.toList());
    }
}
