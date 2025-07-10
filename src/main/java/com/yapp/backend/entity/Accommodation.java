package com.yapp.backend.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "accommodation")
public class Accommodation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메타데이터
    private String urlTest;
    private String siteName;
    private String memo;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long createdBy;
    private Long tableId;

    // 숙소 정보
    private String accommodationName;

    @Type(JsonType.class)
    @Column(columnDefinition = "text[]")
    private List<String> images;

    private String address;
    private Double latitude;
    private Double longitude;
    private Integer lowestPrice;
    private Integer highestPrice;
    private String currency;
    private Double reviewScore;
    private Double cleanlinessScore;
    private String reviewSummary;
    private Long hotelId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String nearbyAttractions;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String nearbyTransportation;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String amenities;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String checkInTime;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String checkOutTime;

}
