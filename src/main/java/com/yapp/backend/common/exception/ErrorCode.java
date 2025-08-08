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
    USER_AUTHORIZATION_FAILED("사용자 권한 없음", "해당 리소스에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // Accommodation related errors
    INVALID_TABLE_ID("잘못된 테이블 ID", "존재하지 않거나 유효하지 않은 테이블 ID입니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_ID("잘못된 사용자 ID", "존재하지 않거나 유효하지 않은 사용자 ID입니다.", HttpStatus.BAD_REQUEST),
    INVALID_URL_FORMAT("잘못된 URL 형식", "유효하지 않은 URL 형식입니다.", HttpStatus.BAD_REQUEST),
    ACCOMMODATION_REGISTRATION_FAILED("숙소 등록 실패", "숙소 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONNECTION_ERROR("데이터베이스 연결 오류", "데이터베이스 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_CONSTRAINT_VIOLATION("데이터베이스 제약 조건 위반", "데이터베이스 제약 조건을 위반했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAGINATION_PARAMETERS("잘못된 페이징 파라미터", "페이지 번호나 크기가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAGING_PARAMETER("잘못된 페이징 파라미터", "페이지 번호는 0 이상이어야 하고, 페이지 크기는 1-100 사이여야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_FACTORS("잘못된 비교 기준", "잘못된 비교 기준입니다.", HttpStatus.BAD_REQUEST),

    // Scraping related errors
    SCRAPING_FAILED("스크래핑 실패", "외부 서버에서 데이터를 가져오는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SCRAPING_SERVER_ERROR("스크래핑 서버 오류", "스크래핑 서버와의 통신에 실패했습니다.", HttpStatus.SERVICE_UNAVAILABLE),

    // Trip Board related errors
    TRIP_BOARD_CREATION_FAILED("여행 보드 생성 실패", "여행 보드 생성에 실패했습니다.", HttpStatus.BAD_REQUEST),
    TRIP_BOARD_PARTICIPANT_LIMIT_EXCEEDED("참여자 수 한계 초과", "여행 보드 참여자 수가 한계에 도달했습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_INVITATION_URL("초대 링크 중복", "초대 링크 생성 중 중복이 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_DESTINATION("유효하지 않은 여행지", "유효하지 않은 여행지입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TRAVEL_PERIOD("유효하지 않은 여행 기간", "유효하지 않은 여행 기간입니다.", HttpStatus.BAD_REQUEST),
    TRIP_BOARD_NOT_FOUND("여행보드 존재하지 않음", "여행보드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRIP_BOARD_UPDATE_FAILED("여행보드 수정 실패", "여행보드 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    TRIP_BOARD_DELETE_FAILED("여행보드 삭제 실패", "여행보드 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // OAuth related errors
    INVALID_AUTHORIZATION_CODE("인가 코드 오류", "인가 코드가 유효하지 않거나 이미 사용되었습니다. 새로운 인가 코드를 발급받아 주세요.", HttpStatus.BAD_REQUEST),
    OAUTH_PARAMETER_ERROR("OAuth 파라미터 오류", "OAuth 요청 파라미터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    OAUTH_CLIENT_AUTH_ERROR("OAuth 클라이언트 인증 오류", "OAuth 클라이언트 인증에 실패했습니다. 클라이언트 정보를 확인해 주세요.", HttpStatus.UNAUTHORIZED),
    OAUTH_TOKEN_EXCHANGE_FAILED("OAuth 토큰 교환 실패", "OAuth 토큰 교환 과정에서 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_OAUTH_BASE_URL("허용되지 않은 Base URL", "OAuth 요청에서 허용되지 않은 Base URL입니다.", HttpStatus.FORBIDDEN),
    UNSUPPORTED_OAUTH_PROVIDER("지원하지 않는 OAuth 공급자", "요청한 OAuth 공급자는 현재 지원되지 않습니다. 지원 가능한 공급자를 확인해 주세요.",
            HttpStatus.BAD_REQUEST)
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