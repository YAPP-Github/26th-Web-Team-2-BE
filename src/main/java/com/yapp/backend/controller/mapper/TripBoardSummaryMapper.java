package com.yapp.backend.controller.mapper;

import com.yapp.backend.controller.dto.response.ParticipantProfileResponse;
import com.yapp.backend.controller.dto.response.TripBoardSummaryResponse;
import com.yapp.backend.service.dto.ParticipantProfile;
import com.yapp.backend.service.dto.TripBoardSummary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TripBoardSummary DTO를 Response 객체로 변환하는 매퍼
 */
@Component
public class TripBoardSummaryMapper {

    /**
     * TripBoardSummary를 TripBoardSummaryResponse로 변환
     * 미리 조회한 참여자 프로필 정보를 사용하여 N+1 문제를 방지합니다.
     * 
     * @param tripBoardSummary       여행 보드 요약 정보
     * @param allParticipantProfiles 모든 참여자 프로필 정보 (미리 조회됨)
     * @return 변환된 응답 DTO
     */
    public TripBoardSummaryResponse toResponse(
            TripBoardSummary tripBoardSummary,
            List<ParticipantProfile> allParticipantProfiles) {

        // 현재 여행 보드의 참여자 프로필 정보만 필터링
        List<ParticipantProfileResponse> participants = allParticipantProfiles.stream()
                .filter(profile -> profile.getTripBoardId().equals(tripBoardSummary.getBoardId()))
                .map(this::toParticipantProfileResponse)
                .collect(Collectors.toList());

        return TripBoardSummaryResponse.builder()
                .boardId(tripBoardSummary.getBoardId())
                .boardName(tripBoardSummary.getBoardName())
                .destination(tripBoardSummary.getDestination())
                .startDate(tripBoardSummary.getStartDate())
                .endDate(tripBoardSummary.getEndDate())
                .userRole(tripBoardSummary.getUserRole())
                .participantCount(participants.size())
                .participants(participants)
                .createdAt(tripBoardSummary.getCreatedAt())
                .updatedAt(tripBoardSummary.getUpdatedAt())
                .build();
    }

    /**
     * ParticipantProfile을 ParticipantProfileResponse로 변환
     * 
     * @param profile 참여자 프로필 정보
     * @return 변환된 참여자 프로필 응답 DTO
     */
    private ParticipantProfileResponse toParticipantProfileResponse(ParticipantProfile profile) {
        return ParticipantProfileResponse.builder()
                .userId(profile.getUserId())
                .profileImageUrl(profile.getProfileImageUrl())
                .nickname(profile.getNickname())
                .role(profile.getRole())
                .build();
    }

    /**
     * TripBoardSummary 리스트를 TripBoardSummaryResponse 리스트로 변환
     * 
     * @param tripBoardSummaries     여행 보드 요약 정보 리스트
     * @param allParticipantProfiles 모든 참여자 프로필 정보
     * @return 변환된 응답 DTO 리스트
     */
    public List<TripBoardSummaryResponse> toResponseList(
            List<TripBoardSummary> tripBoardSummaries,
            List<ParticipantProfile> allParticipantProfiles) {

        return tripBoardSummaries.stream()
                .map(summary -> toResponse(summary, allParticipantProfiles))
                .collect(Collectors.toList());
    }
}