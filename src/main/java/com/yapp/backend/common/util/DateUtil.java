package com.yapp.backend.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 날짜 관련 유틸리티 클래스
 */
public class DateUtil {

    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yy.MM.dd");
    private static final DateTimeFormatter FULL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    /**
     * 여행 기간을 "yy.MM.dd~yy.MM.dd" 형식으로 포맷팅하여 반환
     * 
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 포맷팅된 여행 기간 문자열
     */
    public static String formatTravelPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return "";
        }
        return startDate.format(SHORT_DATE_FORMATTER) + "~" + endDate.format(SHORT_DATE_FORMATTER);
    }

    /**
     * 날짜를 지정된 패턴으로 포맷팅하여 반환
     * 
     * @param date    포맷팅할 날짜
     * @param pattern 날짜 패턴 ("yy.MM.dd" 또는 "yyyy.MM.dd")
     * @return 포맷팅된 날짜 문자열
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * 날짜를 "yy.MM.dd" 형식으로 포맷팅하여 반환
     * 
     * @param date 포맷팅할 날짜
     * @return 포맷팅된 날짜 문자열
     */
    public static String formatShortDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(SHORT_DATE_FORMATTER);
    }

    /**
     * 날짜를 "yyyy.MM.dd" 형식으로 포맷팅하여 반환
     * 
     * @param date 포맷팅할 날짜
     * @return 포맷팅된 날짜 문자열
     */
    public static String formatFullDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FULL_DATE_FORMATTER);
    }
}