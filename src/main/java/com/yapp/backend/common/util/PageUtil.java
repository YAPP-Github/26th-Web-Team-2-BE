package com.yapp.backend.common.util;

import com.yapp.backend.common.exception.InvalidPagingParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 페이징 관련 유틸리티 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PageUtil {

    // todo 페이지 사이즈 제한 확인 및 Controller layer 유효성 검증 확인 필요
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MIN_PAGE_SIZE = 1;

    /**
     * 페이징 파라미터 유효성 검증
     * 
     * @param pageable 검증할 페이징 파라미터
     * @throws InvalidPagingParameterException 페이징 파라미터가 유효하지 않은 경우
     */
    public static void validatePagingParameters(Pageable pageable) {
        validatePageNumber(pageable.getPageNumber());
        validatePageSize(pageable.getPageSize());
    }

    /**
     * 페이지 번호 유효성 검증
     * 
     * @param pageNumber 검증할 페이지 번호
     * @throws InvalidPagingParameterException 페이지 번호가 음수인 경우
     */
    private static void validatePageNumber(int pageNumber) {
        if (pageNumber < 0) {
            log.warn("잘못된 페이지 번호 - 페이지: {}", pageNumber);
            throw new InvalidPagingParameterException();
        }
    }

    /**
     * 페이지 크기 유효성 검증
     * 
     * @param pageSize 검증할 페이지 크기
     * @throws InvalidPagingParameterException 페이지 크기가 유효 범위를 벗어난 경우
     */
    private static void validatePageSize(int pageSize) {
        if (pageSize < MIN_PAGE_SIZE) {
            log.warn("잘못된 페이지 크기 - 크기: {} (최소값: {})", pageSize, MIN_PAGE_SIZE);
            throw new InvalidPagingParameterException();
        }

        if (pageSize > MAX_PAGE_SIZE) {
            log.warn("페이지 크기 한계 초과 - 크기: {} (최대값: {})", pageSize, MAX_PAGE_SIZE);
            throw new InvalidPagingParameterException();
        }
    }
}