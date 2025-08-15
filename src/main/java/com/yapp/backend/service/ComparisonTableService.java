package com.yapp.backend.service;

import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.controller.dto.response.ComparisonTablePageResponse;
import org.springframework.data.domain.Pageable;

public interface ComparisonTableService {

    /**
     * 새로운 비교 테이블을 생성합니다.
     * @param request
     * @param userId
     * @return
     */
    Long createComparisonTable(CreateComparisonTableRequest request, Long userId);

    /**
     * 특정 비교 테이블의 상세 정보를 조회합니다.
     * @param tableId
     * @param userId
     * @return
     */
    ComparisonTableResponse getComparisonTable(Long tableId, Long userId);

    /**
     * 비교테이블의 상세 정보를 수정하고, 성공 여부를 반환합니다.
     * @param tableId
     * @param request
     * @param userId
     * @return
     */
    Boolean updateComparisonTable(Long tableId, UpdateComparisonTableRequest request, Long userId);

    /**
     * 특정 비교 테이블에 새로운 숙소 정보를 추가합니다.
     * @param tableId
     * @param request
     * @param userId
     * @return
     */
    ComparisonTableResponse addAccommodationToComparisonTable(Long tableId, AddAccommodationRequest request, Long userId);

    /**
     * 특졍 비교 테이블을 삭제하고, 관련된 숙소 매핑 정보도 함께 삭제합니다.
     * @param tableId
     * @param userId
     */
    void deleteComparisonTable(Long tableId, Long userId);

    /**
     * 특정 여행보드의 비교표 리스트를 페이지네이션으로 조회합니다.
     * @param tripBoardId 여행보드 ID
     * @param pageable 페이지네이션 정보
     * @return 페이지네이션된 비교표 리스트 응답
     */
    ComparisonTablePageResponse getComparisonTablesByTripBoardId(Long tripBoardId, Pageable pageable);
}
