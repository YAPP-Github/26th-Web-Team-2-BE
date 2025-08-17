package com.yapp.backend.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AccommodationEntity, ComparisonTableEntity 의 매핑 Entity
 */
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "comparison_accommodation",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_cmp_accom",
                columnNames = {"comparison_table_id", "accommodation_id"}
        )
)
public class ComparisonAccommodationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comparison_table_id", nullable = false)
    private ComparisonTableEntity comparisonTableEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private AccommodationEntity accommodationEntity;

    // 숙소 정렬 순서
    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;

    @PrePersist
    public void setDefaultPosition() {
        if (this.position == null) {
            this.position = 0;
            }
    }

    public ComparisonAccommodationEntity(AccommodationEntity accommodationEntity) {
        this.accommodationEntity = accommodationEntity;
    }

    public void updateComparisonTable(ComparisonTableEntity table) {
        this.comparisonTableEntity = table;
    }
}
