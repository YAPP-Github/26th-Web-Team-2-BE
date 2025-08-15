package com.yapp.backend.controller;

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
import com.yapp.backend.controller.dto.response.ComparisonTablePageResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/comparison")
@RequiredArgsConstructor
public class ComparisonTableController implements ComparisonDocs {

    private final ComparisonTableService comparisonTableService;

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
        // TODO: 인증/인가 로직 리팩토링 - 해당 테이블 조회 권한이 있는지 확인 (여행그룹 참여 여부)
        Long userId = userDetails.getUserId();
        ComparisonTableResponse comparisonTableResponse = comparisonTableService.getComparisonTable(tableId,
                userId);
        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS, comparisonTableResponse));
    }

    // TODO: 비교표 수정 API
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
        ComparisonTableResponse response = comparisonTableService.addAccommodationToComparisonTable(tableId,
                request, userId);
        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS, response));
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

    @Override
    @GetMapping("/trip-board/{tripBoardId}")
    public ResponseEntity<StandardResponse<ComparisonTablePageResponse>> getComparisonTablesByTripBoard(
            @PathVariable("tripBoardId") Long tripBoardId,
            @RequestParam Integer page,
            @RequestParam Integer size) {
        
        // 페이징 객체 생성 (최신순 정렬: 수정일 내림차순)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "updatedAt"));
        
        // Service로부터 페이지네이션된 응답 조회
        ComparisonTablePageResponse response = 
                comparisonTableService.getComparisonTablesByTripBoardId(tripBoardId, pageable);
        
        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS, response));
    }

}
