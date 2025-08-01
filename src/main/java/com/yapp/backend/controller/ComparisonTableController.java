package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.ComparisonDocs;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonFactorList;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.controller.dto.response.CreateComparisonTableResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.ComparisonTableService;
import com.yapp.backend.service.model.enums.ComparisonFactor;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                        new ComparisonFactorList(List.of(ComparisonFactor.values()))
                )
        );
    }

    @Override
    @PostMapping("/new")
    public ResponseEntity<StandardResponse<CreateComparisonTableResponse>> createComparisonTable(
            @RequestBody @Valid CreateComparisonTableRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        Long tableId = comparisonTableService.createComparisonTable(request, userId);
        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS, new CreateComparisonTableResponse(tableId))
        );
    }

    @Override
    @GetMapping("/{tableId}")
    public ResponseEntity<StandardResponse<ComparisonTableResponse>> getComparisonTable(
            @PathVariable("tableId") Long tableId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // TODO: 인증/인가 로직 리팩토링 - 해당 테이블 조회 권한이 있는지 확인 (여행그룹 참여 여부)
        Long userId = userDetails.getUserId();
        ComparisonTableResponse comparisonTableResponse = comparisonTableService.getComparisonTable(tableId, userId);
        return ResponseEntity.ok(
                new StandardResponse<>(ResponseType.SUCCESS, comparisonTableResponse)
        );
    }

    // TODO: 비교표 수정 API
    @Override
    @PutMapping("/{tableId}")
    public ResponseEntity<StandardResponse<ComparisonTableResponse>> updateComparisonTable(
            Long tableId, CreateComparisonTableRequest request, CustomUserDetails userDetails) {
        throw new UnsupportedOperationException("아직 구현되지 않은 기능입니다.");
    }

    // TODO: 비교표 숙소 추가 API
    @Override
    @PatchMapping("/{tableId}")
    public ResponseEntity<StandardResponse<ComparisonTableResponse>> addAccommodationToComparisonTable(
            Long tableId, Long accommodationId, CustomUserDetails userDetails) {
        throw new UnsupportedOperationException("아직 구현되지 않은 기능입니다.");
    }

}
