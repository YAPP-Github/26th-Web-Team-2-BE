package com.yapp.backend.common.util;

/**
 * 비교표 이름 자동 생성을 위한 유틸리티 클래스
 */
public class ComparisonTableNameGeneratorUtil {

    /**
     * 시퀀스 번호를 기반으로 비교표 이름을 생성합니다.
     * 형식: "표 {순번}"
     * 
     * @param sequenceNumber 시퀀스 번호
     * @return 자동 생성된 비교표 이름
     */
    public static String generateTableName(int sequenceNumber) {
        return String.format("표 %d", sequenceNumber);
    }

}
