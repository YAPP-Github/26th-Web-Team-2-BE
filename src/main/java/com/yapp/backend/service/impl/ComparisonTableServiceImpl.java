package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.ComparisonTableDeleteException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateAccommodationRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.controller.dto.response.ComparisonTablePageResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableSummaryResponse;
import com.yapp.backend.controller.mapper.ComparisonTableResponseMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.ComparisonTableRepository;
import com.yapp.backend.repository.TripBoardRepository;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.AccommodationService;
import com.yapp.backend.service.ComparisonTableService;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.enums.ComparisonFactor;

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * 비교표 도메인 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComparisonTableServiceImpl implements ComparisonTableService {

    private final ComparisonTableRepository comparisonTableRepository;
    private final TripBoardRepository tripBoardRepository;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationService accommodationService;
    private final ComparisonTableResponseMapper responseMapper;

    @Override
    @Transactional
    public Long createComparisonTable(CreateComparisonTableRequest request, Long userId) {
        // 문자열 리스트를 ComparisonFactor enum으로 변환
        // factorList가 비어있거나 null이면 디폴트 순서로 모든 factor 사용
        List<ComparisonFactor> factors;
        if (request.getFactorList() == null || request.getFactorList().isEmpty()) {
            factors = ComparisonFactor.getDefaultFactors();
        } else {
            factors = ComparisonFactor.convertToComparisonFactorList(request.getFactorList());
        }

        // 실제 TripBoard 데이터를 DB에서 조회
        TripBoard tripBoard = tripBoardRepository.findByIdOrThrow(request.getTripBoardId());

        // 저장할 숙소 리스트 조회
        List<Accommodation> accommodationList = request.getAccommodationIdList().stream()
                .map(accommodationRepository::findByIdOrThrow)
                .toList();

        // 저장하고 생성된 테이블 ID 반환
        return comparisonTableRepository.save(
                ComparisonTable.from(
                        request.getTableName(),
                        userRepository.findByIdOrThrow(userId),
                        tripBoard,
                        accommodationList,
                        factors
                )
        );
    }

    @Override
    public ComparisonTable getComparisonTable(Long tableId) {
        return comparisonTableRepository.findByIdOrThrow(tableId);
    }



    @Override
    @Transactional
    public Boolean updateComparisonTable(
            Long tableId,
            UpdateComparisonTableRequest request,
            Long userId
    ) {
        ComparisonTable existingTable = comparisonTableRepository.findByIdOrThrow(tableId);
        if (isAuthorizedToTable(userId, existingTable)) {
            throw new UserAuthorizationException(ErrorCode.INVALID_USER_AUTHORIZATION);
        }

        // 1. 숙소 세부 내용 업데이트
        updateAccommodationsContent(request.getAccommodationRequestList());

        // 2. ComparisonFactor 정렬 순서 업데이트
        List<ComparisonFactor> updatedFactors = ComparisonFactor.convertToComparisonFactorList(request.getFactorList());

        // 3. Accommodation 정렬 순서에 따라 숙소 리스트 재구성
        List<Accommodation> updatedAccommodationList = request.getAccommodationIdList().stream()
                .map(accommodationRepository::findByIdOrThrow)
                .toList();

        // 4. 업데이트된 비교표 생성
        ComparisonTable updatedTable = ComparisonTable.builder()
                .id(existingTable.getId())
                .tableName(request.getTableName())
                .createdById(existingTable.getCreatedById())
                .tripBoardId(request.getTripBoardId())
                .accommodationList(updatedAccommodationList)
                .factors(updatedFactors)
                .createdAt(existingTable.getCreatedAt())
                .build();

        // 5. 비교표 업데이트 저장
        comparisonTableRepository.update(updatedTable);

        return true;
    }

    private static boolean isAuthorizedToTable(Long userId, ComparisonTable existingTable) {
        return !existingTable.getCreatedById().equals(userId);
    }

    @Override
    @Transactional
    public ComparisonTable addAccommodationToComparisonTable(
            Long tableId,
            AddAccommodationRequest request,
            Long userId
    ) {
        return comparisonTableRepository.addAccommodationsToTable(
                tableId,
                request.getAccommodationIds(),
                userId
        );
    }

    @Override
    @Transactional
    public void deleteComparisonTable(Long tableId, Long userId) {
        try {
            log.info("비교표 삭제 시작 - tableId: {}, userId: {}", tableId, userId);

            // 1. findByIdOrThrow를 통한 비교표 존재 여부 확인
            ComparisonTable comparisonTable = comparisonTableRepository.findByIdOrThrow(tableId);

            // 2. 생성자 권한 검증 로직 구현 (기존 isAuthorizedToTable 메서드 활용)
            if (isAuthorizedToTable(userId, comparisonTable)) {
                log.warn("비교표 삭제 권한 없음 - tableId: {}, userId: {}, createdById: {}",
                        tableId, userId, comparisonTable.getCreatedById());
                throw new ComparisonTableDeleteException(ErrorCode.COMPARISON_TABLE_DELETE_FORBIDDEN);
            }

            // 3. deleteById 인터페이스 호출을 통한 삭제 수행
            comparisonTableRepository.deleteById(tableId);

            log.info("비교표 삭제 완료 - tableId: {}", tableId);

        } catch (ComparisonTableDeleteException e) {
            // 권한 관련 예외는 그대로 재발생
            throw e;
        } catch (Exception e) {
            log.error("비교표 삭제 중 오류 발생 - tableId: {}, userId: {}", tableId, userId, e);
            throw new ComparisonTableDeleteException(ErrorCode.COMPARISON_TABLE_DELETE_FAILED);
        }
    }

    @Override
    public ComparisonTablePageResponse getComparisonTablesByTripBoardId(Long tripBoardId, Pageable pageable) {
        // 여행보드 존재 확인
        tripBoardRepository.findByIdOrThrow(tripBoardId);

        // hasNext 확인을 위해 size + 1개 조회하도록 Pageable 조정
        int requestSize = pageable.getPageSize();
        Pageable adjustedPageable = PageRequest.of(
                pageable.getPageNumber(),
                requestSize + 1,
                pageable.getSort()
        );

        // 페이지네이션된 비교표 리스트 조회
        List<ComparisonTable> comparisonTables = comparisonTableRepository.findByTripBoardId(tripBoardId, adjustedPageable);
        
        // 다음 페이지 존재 여부 확인
        boolean hasNext = comparisonTables.size() > requestSize;
        
        // 실제 반환할 데이터는 요청한 size만큼만
        if (hasNext) {
            comparisonTables = comparisonTables.subList(0, requestSize);
        }
        
        // 매퍼를 사용하여 Response DTO로 변환
        List<ComparisonTableSummaryResponse> responseList = responseMapper.toResponseList(comparisonTables);
        
        // 페이지 응답 객체로 감싸서 반환
        return ComparisonTablePageResponse.of(responseList, hasNext);
    }

    /**
     * 숙소 세부 내용 업데이트
     */
    private void updateAccommodationsContent(
            List<UpdateAccommodationRequest> accommodationRequestList) {
        for (UpdateAccommodationRequest accommodationRequest : accommodationRequestList) {
            if (accommodationRequest.getId() != null) {
                accommodationService.updateAccommodation(accommodationRequest);
            }
        }
    }
}