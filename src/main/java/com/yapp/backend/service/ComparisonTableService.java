package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonTablePageResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import org.springframework.data.domain.Pageable;
import com.yapp.backend.service.model.ComparisonTable;

public interface ComparisonTableService {

    /**
     * 사용자 권한을 검증하고 새로운 비교 테이블을 생성합니다.
     * @param request 비교 테이블 생성 요청
     * @param userId 사용자 ID
     * @return 생성된 비교 테이블 ID
     */
    Long createComparisonTableWithAuthorization(CreateComparisonTableRequest request, Long userId);

    /**
     * 사용자 권한 없이, 비교 테이블의 상세 정보를 조회합니다. (공유 코드를 통한 조회용)
     * @param tableId 비교 테이블 ID
     * @return 비교 테이블 정보
     */
    ComparisonTableResponse getComparisonTable(Long tableId, String shareCode);

    /**
     * 사용자 권한을 검증하고 비교 테이블의 상세 정보를 조회합니다.
     * @param tableId 비교 테이블 ID
     * @param userId 사용자 ID
     * @return 비교 테이블 정보
     */
    ComparisonTableResponse getComparisonTableWithAuthorization(Long tableId, Long userId);

    /**
     * 사용자 권한을 검증하고 비교테이블의 상세 정보를 수정합니다.
     * @param tableId 비교 테이블 ID
     * @param request 수정 요청
     * @param userId 사용자 ID
     * @return 수정 성공 여부
     */
    Boolean updateComparisonTableWithAuthorization(Long tableId, UpdateComparisonTableRequest request, Long userId);

    /**
     * 사용자 권한을 검증하고 특정 비교 테이블에 새로운 숙소 정보를 추가합니다.
     * @param tableId 비교 테이블 ID
     * @param request 숙소 추가 요청
     * @param userId 사용자 ID
     * @return 업데이트된 비교 테이블
     */
    ComparisonTableResponse addAccommodationToComparisonTableWithAuthorization(Long tableId, AddAccommodationRequest request, Long userId);

    /**
     * 사용자 권한을 검증하고 특정 비교 테이블을 삭제합니다.
     * @param tableId 비교 테이블 ID
     * @param userId 사용자 ID
     */
    void deleteComparisonTableWithAuthorization(Long tableId, Long userId);

    /**
     * 사용자 권한을 검증하고 특정 여행보드의 비교표 리스트를 페이지네이션으로 조회합니다.
     * @param tripBoardId 여행보드 ID
     * @param pageable 페이지네이션 정보
     * @param userId 사용자 ID
     * @return 페이지네이션된 비교표 리스트 응답
     */
    ComparisonTablePageResponse getComparisonTablesByTripBoardIdWithAuthorization(Long tripBoardId, Pageable pageable, Long userId);
}
