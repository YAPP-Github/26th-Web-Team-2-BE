package com.yapp.backend.repository.entity;


import static jakarta.persistence.CascadeType.ALL;

import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.ComparisonTable;
import com.yapp.backend.service.model.enums.ComparisonFactor;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comparison_table")
public class ComparisonTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name")
    private String tableName;

    @ManyToOne
    @JoinColumn(name = "trip_board")
    private TripBoardEntity tripBoardEntity;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdByEntity;

    @Type(JsonType.class)
    @Column(name = "factors", columnDefinition = "jsonb")
    private List<ComparisonFactor> factors;

    @Column(name = "share_code", unique = true, nullable = false, length = 32, updatable = false)
    private String shareCode;

    @OneToMany(
            mappedBy = "comparisonTableEntity",
            cascade = ALL,
            orphanRemoval = true
    )
    @OrderBy("position")
    private List<ComparisonAccommodationEntity> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;  // UTC 저장

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // ===== 전용 업데이트 메서드들 (캡슐화 강화) =====
    /**
     * 비교 테이블의 기본 정보와 숙소 매핑 리스트를 업데이트합니다.
     * 기존 매핑을 안전하게 교체합니다.
     */
    public void updateComparisonTableEntity(String tableName, List<ComparisonFactor> factors, List<ComparisonAccommodationEntity> newItems) {
        this.tableName = tableName;
        this.factors = factors;
        this.items.addAll(newItems);
        this.updatedAt = Instant.now();
    }

}
