package com.yapp.backend.service.model;

import com.yapp.backend.controller.dto.request.UpdateAccommodationRequest;
import com.yapp.backend.controller.dto.request.update.AmenityUpdate;
import com.yapp.backend.controller.dto.request.update.AttractionUpdate;
import com.yapp.backend.controller.dto.request.update.CheckTimeUpdate;
import com.yapp.backend.controller.dto.request.update.TransportationUpdate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    private Long id;
    private String url;
    private String siteName;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long boardId;
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

    public Accommodation update(UpdateAccommodationRequest request) {
        // 기본 정보 업데이트 (null이 아닌 경우에만)
        if (request.getMemo() != null) this.memo = request.getMemo();
        if (request.getLowestPrice() != null) this.lowestPrice = request.getLowestPrice();
        if (request.getCurrency() != null) this.currency = request.getCurrency();
        if (request.getReviewScore() != null) this.reviewScore = request.getReviewScore();
        if (request.getCleanlinessScore() != null) this.cleanlinessScore = request.getCleanlinessScore();
        if (request.getReviewSummary() != null) this.reviewSummary = request.getReviewSummary();
        
        // 부분 업데이트 적용
        if (request.getNearbyAttractions() != null) {
            this.nearbyAttractions = updateAttractionsList(this.nearbyAttractions, request.getNearbyAttractions());
        }
        if (request.getNearbyTransportation() != null) {
            this.nearbyTransportation = updateTransportationsList(this.nearbyTransportation, request.getNearbyTransportation());
        }
        if (request.getAmenities() != null) {
            this.amenities = updateAmenitiesList(this.amenities, request.getAmenities());
        }
        if (request.getCheckInTime() != null) {
            this.checkInTime = mapCheckTimeUpdate(request.getCheckInTime());
        }
        if (request.getCheckOutTime() != null) {
            this.checkOutTime = mapCheckTimeUpdate(request.getCheckOutTime());
        }
        
        return this;
    }

    /**
     * 관광지 리스트를 업데이트 요청으로 완전 대체
     * 기존 데이터는 무시하고 request 데이터로 완전히 갈아끼움
     */
    private List<Attraction> updateAttractionsList(List<Attraction> existingAttractions, List<AttractionUpdate> updates) {
        if (updates == null) return existingAttractions;
        
        // request에 들어온 데이터로 완전히 갈아끼움
        return updates.stream()
                .map(update -> Attraction.builder()
                        .name(update.getName())
                        .type(update.getType())
                        .latitude(update.getLatitude())
                        .longitude(update.getLongitude())
                        .distance(update.getDistance())
                        .byFoot(update.getByFoot())
                        .byCar(update.getByCar())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 교통편 리스트를 업데이트 요청으로 완전 대체
     * 기존 데이터는 무시하고 request 데이터로 완전히 갈아끼움
     */
    private List<Transportation> updateTransportationsList(List<Transportation> existingTransportations, List<TransportationUpdate> updates) {
        if (updates == null) return existingTransportations;
        
        // request에 들어온 데이터로 완전히 갈아끼움
        return updates.stream()
                .map(update -> Transportation.builder()
                        .name(update.getName())
                        .type(update.getType())
                        .latitude(update.getLatitude())
                        .longitude(update.getLongitude())
                        .distance(update.getDistance())
                        .byFoot(update.getByFoot())
                        .byCar(update.getByCar())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 기존 편의시설 리스트를 업데이트 요청으로 부분 업데이트
     * type이 같은 항목은 업데이트하고, 나머지는 기존 정보 유지
     */
    private List<Amenity> updateAmenitiesList(List<Amenity> existingAmenities, List<AmenityUpdate> updates) {
        if (existingAmenities == null) existingAmenities = List.of();
        if (updates == null) return existingAmenities;
        
        // 업데이트 요청을 type 기준으로 Map 변환
        Map<String, AmenityUpdate> updateMap = updates.stream()
                .filter(update -> update.getType() != null)
                .collect(Collectors.toMap(AmenityUpdate::getType, update -> update));
        
        // 기존 리스트 업데이트
        List<Amenity> updatedList = existingAmenities.stream()
                .map(existing -> {
                    AmenityUpdate updateRequest = updateMap.get(existing.getType());
                    if (updateRequest != null) {
                        // 매칭되는 업데이트 요청이 있으면 부분 업데이트
                        return Amenity.builder()
                                .type(existing.getType()) // 기존 type 유지
                                .available(updateRequest.isAvailable()) // 업데이트된 available 상태
                                .description(updateRequest.getDescription() != null ? updateRequest.getDescription() : existing.getDescription())
                                .build();
                    } else {
                        // 업데이트 요청이 없으면 기존 정보 그대로 유지
                        return existing;
                    }
                })
                .collect(Collectors.toList());
        
        // 새로운 항목 추가 (기존 리스트에 없는 type)
        Set<String> existingTypes = existingAmenities.stream()
                .map(Amenity::getType)
                .collect(Collectors.toSet());
        
        List<Amenity> newAmenities = updates.stream()
                .filter(update -> update.getType() != null && !existingTypes.contains(update.getType()))
                .map(update -> Amenity.builder()
                        .type(update.getType())
                        .available(update.isAvailable())
                        .description(update.getDescription())
                        .build())
                .collect(Collectors.toList());
        
        updatedList.addAll(newAmenities);
        return updatedList;
    }

    private CheckTime mapCheckTimeUpdate(CheckTimeUpdate update) {
        if (update == null) return null;
        
        return CheckTime.builder()
                .from(update.getFrom())
                .to(update.getTo())
                .build();
    }
}