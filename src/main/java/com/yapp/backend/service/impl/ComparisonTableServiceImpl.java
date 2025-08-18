package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.ComparisonTableDeleteException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateAccommodationRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.ComparisonTablePageResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableSummaryResponse;
import com.yapp.backend.controller.mapper.ComparisonTableResponseMapper;
import com.yapp.backend.service.authorization.UserAccommodationAuthorizationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.ComparisonTableRepository;
import com.yapp.backend.repository.TripBoardRepository;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.AccommodationService;
import com.yapp.backend.service.ComparisonTableService;
import com.yapp.backend.service.authorization.UserComparisonTableAuthorizationService;
import com.yapp.backend.service.authorization.UserTripBoardAuthorizationService;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.enums.ComparisonFactor;

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
    private final UserComparisonTableAuthorizationService authorizationService;
    private final UserTripBoardAuthorizationService tripBoardAuthorizationService;
    private final UserAccommodationAuthorizationService accommodationAuthorizationService;

    // ==================== Public Methods (Controller에서 호출) ====================

    @Override
    @Transactional
    public Long createComparisonTableWithAuthorization(CreateComparisonTableRequest request, Long userId) {
        log.debug("비교 테이블 생성 시작 (권한 검증 포함) - tripBoardId: {}, userId: {}", request.getTripBoardId(), userId);
        
        // 여행보드 멤버 권한 검증
        tripBoardAuthorizationService.validateTripBoardAccessOrThrow(userId, request.getTripBoardId());
        
        // 순수 비즈니스 로직 실행
        return createComparisonTableInternal(request, userId);
    }

    @Override
    public ComparisonTable getComparisonTable(Long tableId) {
        log.debug("비교 테이블 조회 시작 - tableId: {}", tableId);
        
        // 리소스를 한 번만 조회
        ComparisonTable comparisonTable = comparisonTableRepository.findByIdOrThrow(tableId);
        log.debug("비교 테이블 조회 완료 - tableId: {}", tableId);
        return comparisonTable;
    }

    @Override
    public ComparisonTable getComparisonTableWithAuthorization(Long tableId, Long userId) {
        log.debug("비교 테이블 조회 시작 (권한 검증 포함) - tableId: {}, userId: {}", tableId, userId);

        // 리소스를 한 번만 조회
        ComparisonTable comparisonTable = comparisonTableRepository.findByIdOrThrow(tableId);

        // 권한 검증 (엔티티 객체 전달)
        authorizationService.validateReadPermission(comparisonTable, userId);

        log.debug("비교 테이블 조회 완료 (권한 검증 포함) - tableId: {}, userId: {}", tableId, userId);
        return comparisonTable;
    }

    @Override
    @Transactional
    public Boolean updateComparisonTableWithAuthorization(Long tableId, UpdateComparisonTableRequest request, Long userId) {
        log.debug("비교 테이블 수정 시작 (권한 검증 포함) - tableId: {}, userId: {}", tableId, userId);
        
        // 리소스를 한 번만 조회
        ComparisonTable existingTable = comparisonTableRepository.findByIdOrThrow(tableId);
        
        // 권한 검증 (엔티티 객체 전달)
        authorizationService.validateUpdatePermission(existingTable, userId);
        
        // 순수 비즈니스 로직 실행
        return updateComparisonTableInternal(existingTable, request, userId);
    }

    @Override
    @Transactional
    public ComparisonTable addAccommodationToComparisonTableWithAuthorization(Long tableId, AddAccommodationRequest request, Long userId) {
        log.debug("비교 테이블에 숙소 추가 시작 (권한 검증 포함) - tableId: {}, userId: {}", tableId, userId);
        
        // 리소스를 한 번만 조회
        ComparisonTable existingTable = comparisonTableRepository.findByIdOrThrow(tableId);
        
        // 권한 검증 (엔티티 객체 전달)
        authorizationService.validateUpdatePermission(existingTable, userId);
        
        // 순수 비즈니스 로직 실행
        return addAccommodationToComparisonTableInternal(tableId, request, userId);
    }

    @Override
    @Transactional
    public void deleteComparisonTableWithAuthorization(Long tableId, Long userId) {
        log.debug("비교 테이블 삭제 시작 (권한 검증 포함) - tableId: {}, userId: {}", tableId, userId);
        
        try {
            // 리소스를 한 번만 조회
            ComparisonTable comparisonTable = comparisonTableRepository.findByIdOrThrow(tableId);
            
            // 권한 검증 (엔티티 객체 전달)
            authorizationService.validateDeletePermission(comparisonTable, userId);
            
            // 순수 비즈니스 로직 실행
            deleteComparisonTableInternal(tableId, userId);
            
        } catch (UserAuthorizationException e) {
            // 권한 관련 예외는 그대로 재발생
            throw e;
        } catch (Exception e) {
            log.error("비교 테이블 삭제 중 오류 발생 - tableId: {}, userId: {}", tableId, userId, e);
            throw new ComparisonTableDeleteException(ErrorCode.COMPARISON_TABLE_DELETE_FAILED);
        }
    }

    @Override
    public ComparisonTablePageResponse getComparisonTablesByTripBoardIdWithAuthorization(Long tripBoardId, Pageable pageable, Long userId) {
        log.debug("여행보드 비교 테이블 목록 조회 시작 (권한 검증 포함) - tripBoardId: {}, page: {}, size: {}, userId: {}", 
                tripBoardId, pageable.getPageNumber(), pageable.getPageSize(), userId);
        
        // 여행보드 멤버 권한 검증
        tripBoardAuthorizationService.validateTripBoardAccessOrThrow(userId, tripBoardId);
        
        // 순수 비즈니스 로직 실행
        return getComparisonTablesByTripBoardIdInternal(tripBoardId, pageable);
    }

    // ==================== Private Methods (내부 비즈니스 로직) ====================

    /**
     * 새로운 비교 테이블을 생성하는 순수 비즈니스 로직
     * 
     * @param request 비교 테이블 생성 요청
     * @param userId 사용자 ID
     * @return 생성된 비교 테이블 ID
     */
    private Long createComparisonTableInternal(CreateComparisonTableRequest request, Long userId) {
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
        
        // 요청된 숙소들이 해당 여행보드에 속하는지 확인
        for (Accommodation accommodation : accommodationList) {
            accommodationAuthorizationService.validateAccommodationBelongsToTripBoardOrThrow(
                accommodation, 
                tripBoard.getId()
            );
        }

        // 저장하고 생성된 테이블 ID 반환
        Long tableId = comparisonTableRepository.save(
                ComparisonTable.from(
                        request.getTableName(),
                        userRepository.findByIdOrThrow(userId),
                        tripBoard,
                        accommodationList,
                        factors
                )
        ).getId();
        
        log.info("비교 테이블 생성 완료 - tableId: {}, tripBoardId: {}, userId: {}", tableId, request.getTripBoardId(), userId);
        return tableId;
    }

    /**
     * 비교 테이블을 수정하는 순수 비즈니스 로직
     * 
     * @param existingTable 기존 비교 테이블 엔티티
     * @param request 수정 요청
     * @param userId 사용자 ID
     * @return 수정 성공 여부
     */
    private Boolean updateComparisonTableInternal(ComparisonTable existingTable, UpdateComparisonTableRequest request, Long userId) {
        log.debug("비교 테이블 수정 시작 - tableId: {}, userId: {}", existingTable.getId(), userId);

        // 요청된 숙소들이 해당 여행보드에 속하는지 확인
        // 숙소가 존재하지 않으면 404
        // 숙소가 여행 보드에 속하지 않으면 400
        if (request.getAccommodationIdList() != null && !request.getAccommodationIdList().isEmpty()) {
            // 먼저 숙소들을 조회
            List<Accommodation> accommodationList = request.getAccommodationIdList().stream()
                    .map(accommodationRepository::findByIdOrThrow)
                    .toList();
            
            // 조회된 숙소들을 사용하여 검증 (중복 조회 방지)
            for (Accommodation accommodation : accommodationList) {
                accommodationAuthorizationService.validateAccommodationBelongsToTripBoardOrThrow(
                    accommodation, 
                    existingTable.getTripBoardId()
                );
            }
        }

        // 1. 숙소 세부 내용 업데이트
        updateAccommodationsContent(request.getAccommodationRequestList());

        // 2. 도메인 객체 내부 메서드로 ComparisonFactor 정렬 순서, Accommodation 정렬 순서 업데이트
        List<Accommodation> updatedAccommodationList = request.getAccommodationIdList().stream()
                .map(accommodationRepository::findByIdOrThrow)
                .toList();
        List<ComparisonFactor> updatedFactors = ComparisonFactor.convertToComparisonFactorList(request.getFactorList());
        existingTable.updateTable(request.getTableName(), updatedAccommodationList, updatedFactors);

        comparisonTableRepository.update(existingTable);

        log.info("비교 테이블 수정 완료 - tableId: {}, userId: {}", existingTable.getId(), userId);
        return true;
    }

    /**
     * 비교 테이블에 숙소를 추가하는 순수 비즈니스 로직
     * 
     * @param tableId 비교 테이블 ID
     * @param request 숙소 추가 요청
     * @param userId 사용자 ID
     * @return 업데이트된 비교 테이블
     */
    private ComparisonTable addAccommodationToComparisonTableInternal(Long tableId, AddAccommodationRequest request, Long userId) {
        log.debug("비교 테이블에 숙소 추가 시작 - tableId: {}, userId: {}", tableId, userId);


        // 기존 비교 테이블 조회
        ComparisonTable comparisonTable = comparisonTableRepository.findByIdOrThrow(tableId);

        // 추가하려는 숙소들이 해당 여행보드에 속하는지 확인
        if (request.getAccommodationIds() != null && !request.getAccommodationIds().isEmpty()) {
            // 먼저 숙소들을 조회
            List<Accommodation> accommodationList = request.getAccommodationIds().stream()
                    .map(accommodationRepository::findByIdOrThrow)
                    .toList();

            // 조회된 숙소들을 사용하여 검증
            for (Accommodation accommodation : accommodationList) {
                accommodationAuthorizationService.validateAccommodationBelongsToTripBoardOrThrow(
                    accommodation,
                    comparisonTable.getTripBoardId()
                );
            }
        }

        return comparisonTableRepository.addAccommodationsToTable(
                tableId,
                request.getAccommodationIds(),
                userId
        );
    }

    /**
     * 비교 테이블을 삭제하는 순수 비즈니스 로직
     * 
     * @param tableId 비교 테이블 ID
     * @param userId 사용자 ID
     */
    private void deleteComparisonTableInternal(Long tableId, Long userId) {
        log.debug("비교 테이블 삭제 시작 - tableId: {}, userId: {}", tableId, userId);
        
        comparisonTableRepository.deleteById(tableId);

        log.info("비교 테이블 삭제 완료 - tableId: {}", tableId);
    }

    /**
     * 여행보드의 비교 테이블 목록을 페이지네이션으로 조회하는 순수 비즈니스 로직
     * 
     * @param tripBoardId 여행보드 ID
     * @param pageable 페이지네이션 정보
     * @return 페이지네이션된 비교표 리스트 응답
     */
    private ComparisonTablePageResponse getComparisonTablesByTripBoardIdInternal(Long tripBoardId, Pageable pageable) {
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
        ComparisonTablePageResponse response = ComparisonTablePageResponse.of(responseList, hasNext);
        
        log.debug("여행보드 비교 테이블 목록 조회 완료 - tripBoardId: {}, 조회된 개수: {}, hasNext: {}", 
                tripBoardId, responseList.size(), hasNext);
        return response;
    }

    /**
     * 숙소 세부 내용을 업데이트하는 헬퍼 메서드
     * 
     * @param accommodationRequestList 업데이트할 숙소 요청 리스트
     */
    private void updateAccommodationsContent(List<UpdateAccommodationRequest> accommodationRequestList) {
        for (UpdateAccommodationRequest accommodationRequest : accommodationRequestList) {
            if (accommodationRequest.getId() != null) {
                accommodationService.updateAccommodation(accommodationRequest);
            }
        }
    }
}