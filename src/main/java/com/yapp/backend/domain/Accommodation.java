package com.yapp.backend.domain;

import com.yapp.backend.repository.entity.Amenity;
import com.yapp.backend.repository.entity.Attraction;
import com.yapp.backend.repository.entity.CheckTime;
import com.yapp.backend.repository.entity.Transportation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    private Long id;
    private String urlTest;
    private String siteName;
    private String memo;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long createdBy;
    private Long tableId;
    private String accommodationName;
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
    private List<Attraction> nearbyAttractions;
    private List<Transportation> nearbyTransportation;
    private List<Amenity> amenities;
    private CheckTime checkInTime;
    private CheckTime checkOutTime;
}