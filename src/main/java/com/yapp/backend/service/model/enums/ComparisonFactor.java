package com.yapp.backend.service.model.enums;

import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.InvalidFactorsException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ComparisonFactor {
    REVIEW_SCORE,               // 리뷰 점수
    ATTRACTION,                 // 인근 관광지
    TRANSPORTATION,             // 인근 교통편
    CLEANLINESS,                // 청결도
    AMENITY,                    // 편의 서비스
    CHECK_TIME,                 // 체크인, 체크아웃 시간
    REVIEW_SUMMARY,             // 리뷰 요약
    MEMO                        // 메모
    ;

    /**
     * 문자열 리스트를 ComparisonFactor 리스트로 변환
     */
    public static List<ComparisonFactor> convertToComparisonFactorList(List<String> factorNames) {
        return factorNames.stream()
                .map(name -> Arrays.stream(ComparisonFactor.values())
                        .filter(f -> f.name().equals(name))
                        .findFirst()
                        .orElseThrow(() -> new InvalidFactorsException(ErrorCode.INVALID_FACTORS))
                )
                .collect(Collectors.toList());
    }
}
