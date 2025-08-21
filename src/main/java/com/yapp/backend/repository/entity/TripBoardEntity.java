package com.yapp.backend.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trip_board")
public class TripBoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_name")
    private String boardName;

    @Column(name = "destination", length = 20, nullable = false)
    private String destination;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private UserEntity updatedBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "next_comparison_table_number", nullable = false)
    @Builder.Default
    private Integer nextComparisonTableNumber = 1;

    @OneToMany(mappedBy = "tripBoardEntity"
    // cascade = CascadeType.ALL,
    // orphanRemoval = true
    )
    @Builder.Default
    private List<ComparisonTableEntity> comparisonTables = new ArrayList<>();

    /**
     * 여행보드 정보를 업데이트합니다.
     */
    public void updateTripBoard(String boardName, String destination, LocalDate startDate, LocalDate endDate,
            UserEntity updatedBy) {
        this.boardName = boardName;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedBy = updatedBy;
    }
}
