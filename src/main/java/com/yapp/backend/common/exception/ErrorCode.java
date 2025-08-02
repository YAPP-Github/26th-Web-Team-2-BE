package com.yapp.backend.common.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCode {
    CUSTOM_ERROR("커스텀 에러", "이것은 커스텀 예외입니다.", HttpStatus.BAD_REQUEST),

    /**
     * 4xx
     */
    USER_NOT_FOUND("사용자 존재하지 않음", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TABLE_NOT_FOUND("비교표 존재하지 않음", "비교표를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ACCOMMODATION_NOT_FOUND("숙소 정보 존재하지 않음", "숙소 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // Authorization
    INVALID_USER_AUTHORIZATION("사용자 권한 없음", "해당 권한이 없는 유저입니다.", HttpStatus.FORBIDDEN),

    // Accommodation related errors
    INVALID_TABLE_ID("잘못된 테이블 ID", "존재하지 않거나 유효하지 않은 테이블 ID입니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID("잘못된 사용자 ID", "존재하지 않거나 유효하지 않은 사용자 ID입니다.", HttpStatus.BAD_REQUEST),
    INVALID_URL_FORMAT("잘못된 URL 형식", "유효하지 않은 URL 형식입니다.", HttpStatus.BAD_REQUEST),
    ACCOMMODATION_REGISTRATION_FAILED("숙소 등록 실패", "숙소 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONNECTION_ERROR("데이터베이스 연결 오류", "데이터베이스 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONSTRAINT_VIOLATION("데이터베이스 제약 조건 위반", "데이터베이스 제약 조건을 위반했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAGINATION_PARAMETERS("잘못된 페이징 파라미터", "페이지 번호나 크기가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FACTORS("잘못된 비교 기준", "잘못된 비교 기준입니다.", HttpStatus.BAD_REQUEST),

    // Scraping related errors
    SCRAPING_FAILED("스크래핑 실패", "외부 서버에서 데이터를 가져오는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SCRAPING_SERVER_ERROR("스크래핑 서버 오류", "스크래핑 서버와의 통신에 실패했습니다.", HttpStatus.SERVICE_UNAVAILABLE)
    // 필요에 따라 추가
    ;

    private final String title;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String title, String message, HttpStatus httpStatus) {
        this.title = title;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}