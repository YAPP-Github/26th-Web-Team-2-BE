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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    @OneToMany(
            mappedBy = "comparisonTableEntity",
            cascade = ALL,
            orphanRemoval = true
    )
    @OrderBy("position")
    private List<ComparisonAccommodationEntity> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ComparisonTableEntity from(ComparisonTable comparisonTable) {
        ComparisonTableEntity tableEntity = ComparisonTableEntity.builder()
                .tableName(comparisonTable.getTableName())
                .tripBoardEntity(new TripBoardEntity(comparisonTable.getId()))
                .createdByEntity(new UserEntity(comparisonTable.getCreatedById()))
                .items(new ArrayList<>())
                .factors(comparisonTable.getFactors())
                .build();
        for (int i = 0; i < comparisonTable.getAccommodationList().size(); i++) {
            Accommodation accommodation = comparisonTable.getAccommodationList().get(i);
            ComparisonAccommodationEntity itemEntity = ComparisonAccommodationEntity.builder()
                    .accommodationEntity(AccommodationEntity.from(accommodation))
                    .position(i).build();
            itemEntity.setComparisonTable(tableEntity);
            tableEntity.items.add(itemEntity);
        }

        return tableEntity;
    }

    public ComparisonTable toDomain() {
        return ComparisonTable.builder()
                .id(this.id)
                .tableName(this.tableName)
                .tripBoardId(this.tripBoardEntity.getId())
                .createdById(this.createdByEntity.getId())
                .accommodationList(
                        this.items.stream()
                                .map(item -> item.getAccommodationEntity().toDomain()).collect(
                        Collectors.toList()))
                .factors(this.factors)
                .build();
    }

}
