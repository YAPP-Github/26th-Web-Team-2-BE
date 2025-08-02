package com.yapp.backend.service.impl;

import com.yapp.backend.controller.dto.request.CreateComparisonTableRequest;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.controller.dto.response.ComparisonTableResponse;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.repository.ComparisonTableRepository;
import com.yapp.backend.repository.UserRepository;
import com.yapp.backend.service.ComparisonTableService;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.User;
import com.yapp.backend.service.model.enums.ComparisonFactor;
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

    @Override
    @Transactional
    public Long createComparisonTable(CreateComparisonTableRequest request, Long userId) {
        // 문자열 리스트를 ComparisonFactor enum으로 변환
        List<ComparisonFactor> factors = ComparisonFactor.convertToComparisonFactorList(request.getFactorList());

        // TODO : request boardId에서 DB에서 실제 trip board data 가져오기
        TripBoard tripBoard = TripBoard.builder()
                .id(request.getBoardId())
                .boardName("테스트 여행 그룹 " + request.getBoardId())
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
                        tripBoard,
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
} 