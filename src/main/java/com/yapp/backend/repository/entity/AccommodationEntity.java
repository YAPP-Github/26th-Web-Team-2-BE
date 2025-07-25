package com.yapp.backend.repository.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;

import com.yapp.backend.service.model.Amenity;
import com.yapp.backend.service.model.Attraction;
import com.yapp.backend.service.model.CheckTime;
import com.yapp.backend.service.model.Transportation;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accommodation")
public class AccommodationEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메타데이터
    @Column(name = "url_test")
    private String urlTest;
    @Column(name = "site_name")
    private String siteName;
    @Column(name = "memo")
    private String memo;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "table_id")
    private Long tableId;

    // 숙소 정보
    @Column(name = "accommodation_name")
    private String accommodationName;

    @Type(JsonType.class)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<String> images;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "lowest_price")
    private Integer lowestPrice;
    @Column(name = "highest_price")
    private Integer highestPrice;
    @Column(name = "currency", length = 3)
    private String currency;
    @Column(name = "review_score")
    private Double reviewScore;
    @Column(name = "cleanliness_score")
    private Double cleanlinessScore;
    @Column(name = "review_summary")
    private String reviewSummary;
    @Column(name = "hotel_id")
    private Long hotelId;

    @Type(JsonType.class)
    @Column(name = "nearby_attractions", columnDefinition = "jsonb")
    private List<Attraction> nearbyAttractions;

    @Type(JsonType.class)
    @Column(name = "nearby_transportation", columnDefinition = "jsonb")
    private List<Transportation> nearbyTransportation;

    @Type(JsonType.class)
    @Column(name = "amenities", columnDefinition = "jsonb")
    private List<Amenity> amenities;

    @Type(JsonType.class)
    @Column(name = "check_in_time", columnDefinition = "jsonb")
    private CheckTime checkInTime;

    @Type(JsonType.class)
    @Column(name = "check_out_time", columnDefinition = "jsonb")
    private CheckTime checkOutTime;
}
