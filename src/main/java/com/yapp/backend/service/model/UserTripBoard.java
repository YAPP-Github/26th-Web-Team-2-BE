package com.yapp.backend.service.model;

import com.yapp.backend.repository.enums.TripBoardRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTripBoard {
    private Long id;
    private User user;
    private TripBoard tripBoard;
    private String invitationCode;
    private Boolean invitationActive;
    private TripBoardRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 초대 링크 활성화 상태를 토글합니다.
     * 현재 상태의 반대로 변경된 새로운 UserTripBoard 객체를 반환합니다.
     * 
     * @return 토글된 상태의 새로운 UserTripBoard 객체
     */
    public UserTripBoard toggleInvitationActive() {
        return UserTripBoard.builder()
                .id(this.id)
                .user(this.user)
                .tripBoard(this.tripBoard)
                .invitationCode(this.invitationCode)
                .invitationActive(!this.invitationActive)
                .role(this.role)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}