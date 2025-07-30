package com.yapp.backend.repository.enums;

import lombok.Getter;

/**
 * 숙소 목록 정렬 타입을 정의하는 enum
 */
@Getter
public enum SortType {
    SAVED_AT_DESC("saved_at_desc"),
    PRICE_ASC("price_asc");

    private final String value;

    SortType(String value) {
        this.value = value;
    }

    /**
     * 문자열 값으로부터 SortType을 찾아 반환
     * 매칭되는 값이 없으면 기본값(saved_at_desc) 반환
     */
    public static SortType fromString(String value) {
        if (value == null) {
            return SAVED_AT_DESC;
        }

        for (SortType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return SAVED_AT_DESC; // 기본값
    }
}