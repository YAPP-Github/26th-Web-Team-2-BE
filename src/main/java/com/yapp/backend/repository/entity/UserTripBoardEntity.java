package com.yapp.backend.repository.entity;

import com.yapp.backend.repository.enums.TripBoardRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_trip_board")
public class UserTripBoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_board_id", nullable = false)
    private TripBoardEntity tripBoard;

    // todo 초대링크가 아니라 코드로 변경 필
    @Column(name = "invitation_url", unique = true, nullable = false)
    private String invitationUrl;

    @Column(name = "invitation_active", nullable = false)
    @Builder.Default
    private Boolean invitationActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private TripBoardRole role = TripBoardRole.MEMBER;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
