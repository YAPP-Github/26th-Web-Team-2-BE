package com.yapp.backend.controller.mapper;

import com.yapp.backend.controller.dto.request.TripBoardUpdateRequest;
import com.yapp.backend.controller.dto.response.TripBoardUpdateResponse;
import com.yapp.backend.service.model.TripBoard;
import com.yapp.backend.service.model.User;
import org.springframework.stereotype.Component;

/**
 * TripBoardUpdate 관련 DTO와 Domain 모델 간의 변환을 담당하는 매퍼
 */
@Component
public class TripBoardUpdateMapper {

    /**
     * TripBoardUpdateRequest를 TripBoard 도메인 모델로 변환
     *
     * @param request     업데이트 요청 DTO
     * @param tripBoardId 업데이트할 여행보드 ID
     * @param updatedBy   업데이트를 수행하는 사용자
     * @return 변환된 TripBoard 도메인 모델
     */
    public TripBoard requestToDomain(TripBoardUpdateRequest request, Long tripBoardId, User updatedBy) {
        if (request == null) {
            return null;
        }

        return TripBoard.builder()
                .id(tripBoardId)
                .boardName(request.getBoardName())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .updatedBy(updatedBy)
                .build();
    }

    /**
     * TripBoard 도메인 모델을 TripBoardUpdateResponse로 변환
     *
     * @param tripBoard 업데이트된 여행보드 도메인 모델
     * @return 변환된 응답 DTO
     */
    public TripBoardUpdateResponse domainToResponse(TripBoard tripBoard) {
        if (tripBoard == null) {
            return null;
        }

        return TripBoardUpdateResponse.builder()
                .tripBoardId(tripBoard.getId())
                .boardName(tripBoard.getBoardName())
                .destination(tripBoard.getDestination())
                .startDate(tripBoard.getStartDate())
                .endDate(tripBoard.getEndDate())
                .updatedAt(tripBoard.getUpdatedAt())
                .build();
    }
}