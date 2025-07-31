package com.yapp.backend.service.impl;

import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.exception.UserAuthorizationException;
import com.yapp.backend.controller.dto.request.AddAccommodationRequest;
import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.request.UpdateAccommodationRequest;
import com.yapp.backend.controller.dto.request.UpdateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.ComparisonTableRepository;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.AccommodationService;
import com.yapp.backend.service.ComparisonTableService;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.User;
import com.yapp.backend.service.model.enums.ComparisonFactor;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * 비교표 도메인 서비스
 */
@Service
@RequiredArgsConstructor
public class ComparisonTableServiceImpl implements ComparisonTableService {

    private final ComparisonTableRepository comparisonTableRepository;
//    private final TripGroupRepository tripGroupRepository;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationService accommodationService;

    @Override
    @Transactional
    public Long createComparisonTable(CreateComparisonTableRequest request, Long userId) {
        // 문자열 리스트를 ComparisonFactor enum으로 변환
        List<ComparisonFactor> factors = ComparisonFactor.convertToComparisonFactorList(request.getFactorList());

        // TODO : request boardId에서 DB에서 실제 trip board data 가져오기
        TripBoard tripGroup = TripBoard.builder()
                .id(request.getBoardId())
                .name("테스트 여행 그룹 " + request.getBoardId())
                .createdBy(User.builder()
                        .id(userId)
                        .nickname("그룹생성자" + userId)
                        .build())
                .build();

        // 저장할 숙소 리스트 조회
        List<Accommodation> accommodationList = request.getAccommodationIdList().stream()
                .map(accommodationRepository::findByIdOrThrow)
                .toList();

        // 저장하고 생성된 테이블 ID 반환
        return comparisonTableRepository.save(
                ComparisonTable.from(
                        request.getTableName(),
                        userRepository.findByIdOrThrow(userId),
                        tripGroup,
                        accommodationList,
                        factors
                )
        );
    }

    @Override
    public ComparisonTableResponse getComparisonTable(Long tableId, Long userId) {
        ComparisonTable comparisonTable = comparisonTableRepository.findByIdOrThrow(tableId);
        return new ComparisonTableResponse(
                comparisonTable.getId(),
                comparisonTable.getTableName(),
                comparisonTable.getAccommodationList().stream().map(AccommodationResponse::from).collect(
                        Collectors.toList()),
                comparisonTable.getFactors(),
                comparisonTable.getCreatedById()
        );
    }

    @Override
    @Transactional
    public Boolean updateComparisonTable(
            Long tableId,
            UpdateComparisonTableRequest request,
            Long userId
    ) {
        try {
            // 기존 비교표 조회
            ComparisonTable existingTable = comparisonTableRepository.findByIdOrThrow(tableId);

            // 권한 검증: 해당 비교표를 생성한 사용자인지 확인
            if (!existingTable.getCreatedById().equals(userId)) {
                throw new UserAuthorizationException(ErrorCode.INVALID_USER_AUTHORIZATION);
            }

            // 1. 숙소 세부 내용 업데이트
            if (request.getAccommodationRequestList() != null) {
                updateAccommodationsContent(request.getAccommodationRequestList());
            }
            
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
                    .tripBoardId(request.getBoardId())
                    .accommodationList(updatedAccommodationList)
                    .factors(updatedFactors)
                    .createdAt(existingTable.getCreatedAt())
                    .build();
            
            // 5. 비교표 업데이트 저장
            comparisonTableRepository.update(updatedTable);
            
            return true;
            
        } catch (Exception e) {
            // 에러 로깅 및 예외 처리
            throw new RuntimeException("비교표 업데이트 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public ComparisonTableResponse addAccommodationToComparisonTable(
            Long tableId,
            AddAccommodationRequest request,
            Long userId
    ) {
        try {
            ComparisonTable updatedTable = comparisonTableRepository.addAccommodationsToTable(
                    tableId, 
                    request.getAccommodationIds(), 
                    userId
            );
            
            // 업데이트된 비교표 반환
            return new ComparisonTableResponse(
                    updatedTable.getId(),
                    updatedTable.getTableName(),
                    updatedTable.getAccommodationList().stream()
                            .map(AccommodationResponse::from)
                            .collect(Collectors.toList()),
                    updatedTable.getFactors(),
                    updatedTable.getCreatedById()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("비교표에 숙소 추가 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 숙소 세부 내용 업데이트
     */
    private void updateAccommodationsContent(List<UpdateAccommodationRequest> accommodationRequestList) {
        for (UpdateAccommodationRequest accommodationRequest : accommodationRequestList) {
            if (accommodationRequest.getId() != null) {
                accommodationService.updateAccommodation(accommodationRequest);
            }
        }
    }
} 