package com.yapp.backend.controller;

import static com.yapp.backend.common.exception.ErrorCode.*;

import com.yapp.backend.common.exception.ShareCodeException;
import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.ComparisonDocs;
import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.AmenityFactorList;
import com.yapp.backend.controller.dto.response.ComparisonFactorList;
import com.yapp.backend.controller.dto.response.ComparisonTableDeleteResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.controller.dto.response.CreateComparisonTableResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.ComparisonTableService;
import com.yapp.backend.service.model.enums.AmenityFactor;
import com.yapp.backend.service.model.enums.ComparisonFactor;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yapp.backend.service.model.ComparisonTable;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.UserAuthorizationException;
import lombok.extern.slf4j.Slf4j;
import com.yapp.backend.controller.mapper.ComparisonTableResponseMapper;

@Slf4j
@RestController
@RequestMapping("/api/comparison")
@RequiredArgsConstructor
public class ComparisonTableController implements ComparisonDocs {

    private final ComparisonTableService comparisonTableService;
    private final ComparisonTableResponseMapper comparisonTableResponseMapper;

    @Override
    @GetMapping("/factors")
    public ResponseEntity<StandardResponse<ComparisonFactorList>> getComparisonFactorList() {
        return ResponseEntity.ok(
                new StandardResponse<>(
                        ResponseType.SUCCESS,
                        new ComparisonFactorList(List.of(ComparisonFactor.values()))));
    }

    @Override
    @GetMapping("/amenity")
    public ResponseEntity<StandardResponse<AmenityFactorList>> getAmenityFactorList() {
        return ResponseEntity.ok(
                new StandardResponse<>(
                        ResponseType.SUCCESS,
                        new AmenityFactorList(List.of(AmenityFactor.values()))));
    }

    // TODO: 비교 테이블 관련 권한 검증
    @Override
    @PostMapping("/new")
    public ResponseEntity<StandardResponse<CreateComparisonTableResponse>> createComparisonTable(
            @RequestBody @Valid CreateComparisonTableRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        Long tableId = comparisonTableService.createComparisonTable(request, userId);
        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS,
                        new CreateComparisonTableResponse(tableId)));
    }

    @Override
    @GetMapping("/{tableId}")
    public ResponseEntity<StandardResponse<ComparisonTableResponse>> getComparisonTable(
            @PathVariable("tableId") Long tableId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ComparisonTable comparisonTable = comparisonTableService.getComparisonTable(tableId);
        ComparisonTableResponse response = comparisonTableResponseMapper.toResponse(comparisonTable);
        
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * shareCode를 통한 비교표 조회 (인증 불필요)
     */
    @GetMapping("/{tableId}/shared")
    public ResponseEntity<StandardResponse<ComparisonTableResponse>> getComparisonTableByShareCode(
            @PathVariable("tableId") Long tableId,
            @RequestParam("shareCode") String shareCode) {

        ComparisonTable comparisonTable = comparisonTableService.getComparisonTable(tableId);
        
        // shareCode 검증
        validateShareCodeAccess(tableId, shareCode, comparisonTable);
        ComparisonTableResponse response = comparisonTableResponseMapper.toResponse(comparisonTable);
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    /**
     * shareCode를 통한 접근 권한 검증
     */
    private void validateShareCodeAccess(Long tableId, String shareCode, ComparisonTable comparisonTable) {
        if (!shareCode.equals(comparisonTable.getShareCode())) {
            log.warn("유효하지 않은 shareCode로 비교표 조회 시도 - tableId: {}, shareCode: {}", tableId, shareCode);
            throw new ShareCodeException(INVALID_SHARE_CODE);
        }
        log.info("shareCode를 통한 비교표 조회 성공 - tableId: {}, shareCode: {}", tableId, shareCode);
    }

    @Override
    @PutMapping("/{tableId}")
    public ResponseEntity<StandardResponse<Boolean>> updateComparisonTable(
            @PathVariable("tableId") Long tableId,
            @RequestBody UpdateComparisonTableRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        Boolean isUpdated = comparisonTableService.updateComparisonTable(tableId, request, userId);
        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS, isUpdated));
    }

    @Override
    @PatchMapping("/{tableId}")
    public ResponseEntity<StandardResponse<ComparisonTableResponse>> addAccommodationToComparisonTable(
            @PathVariable("tableId") Long tableId,
            @RequestBody AddAccommodationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        
        ComparisonTable comparisonTable = comparisonTableService.addAccommodationToComparisonTable(tableId, request, userId);
        ComparisonTableResponse response = comparisonTableResponseMapper.toResponse(comparisonTable);
        
        return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
    }

    @Override
    @DeleteMapping("/{tableId}")
    public ResponseEntity<StandardResponse<ComparisonTableDeleteResponse>> deleteComparisonTable(
            @PathVariable("tableId") Long tableId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        comparisonTableService.deleteComparisonTable(tableId, userId);

        ComparisonTableDeleteResponse response = ComparisonTableDeleteResponse.builder()
                .tableId(tableId)
                .message("비교표가 성공적으로 삭제되었습니다.")
                .build();

        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS, response));
    }

}
