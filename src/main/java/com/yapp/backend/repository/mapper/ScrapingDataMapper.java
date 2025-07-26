package com.yapp.backend.repository.mapper;

import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.service.dto.ScrapingData;
import com.yapp.backend.service.dto.ScrapingAmenity;
import com.yapp.backend.service.dto.ScrapingAttraction;
import com.yapp.backend.service.dto.ScrapingTransportation;
import com.yapp.backend.service.dto.ScrapingCheckTime;
import com.yapp.backend.service.model.Amenity;
import com.yapp.backend.service.model.Attraction;
import com.yapp.backend.service.model.Transportation;
import com.yapp.backend.service.model.CheckTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScrapingDataMapper {

    /**
     * 스크래핑 데이터 -> 숙소 데이터 모델 매핑
     */
    public AccommodationEntity mapToEntity(ScrapingData scrapingData, String originalUrl, String memo, Long createdBy, Long tableId) {
        if (scrapingData == null) {
            log.warn("ScrapingData is null, creating entity with minimal data");
            return createMinimalEntity(originalUrl, memo, createdBy, tableId);
        }

        return AccommodationEntity.builder()
                .url(originalUrl)
                .siteName(scrapingData.getSiteName())
                .memo(memo)
                .createdBy(createdBy)
                .tableId(tableId)
                .accommodationName(scrapingData.getAccommodationName())
                .images(scrapingData.getImage())
                .address(scrapingData.getAddress())
                .latitude(scrapingData.getLatitude())
                .longitude(scrapingData.getLongitude())
                .lowestPrice(parsePrice(scrapingData.getLowestPrice()))
                .highestPrice(parsePrice(scrapingData.getHighestPrice()))
                .currency(scrapingData.getCurrency())
                .reviewScore(scrapingData.getReviewScore())
                .cleanlinessScore(scrapingData.getCleanlinessScore())
                .reviewSummary(scrapingData.getReviewSummary())
                .hotelId(parseHotelId(scrapingData.getHotelId()))
                .nearbyAttractions(mapAttractions(scrapingData.getNearbyAttractions()))
                .nearbyTransportation(mapTransportations(scrapingData.getNearbyTransportation()))
                .amenities(mapAmenities(scrapingData.getAmenities()))
                .checkInTime(mapCheckInTime(scrapingData.getCheckInTime()))
                .checkOutTime(mapCheckOutTime(scrapingData.getCheckOutTime()))
                .build();
    }

    /**
     * 숙소 데이터 모델 생성
     */
    private AccommodationEntity createMinimalEntity(String url, String memo, Long createdBy, Long tableId) {
        return AccommodationEntity.builder()
                .url(url)
                .memo(memo)
                .createdBy(createdBy)
                .tableId(tableId)
                .accommodationName("정보 없음")
                .siteName(extractSiteNameFromUrl(url))
                .currency("KRW")
                .build();
    }

    /**
     * URL에서 사이트명을 추출합니다.
     */
    private String extractSiteNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "알 수 없음";
        }
        
        try {
            if (url.contains("agoda")) {
                return "agoda";
            } else if (url.contains("booking")) {
                return "booking";
            } else if (url.contains("airbnb")) {
                return "airbnb";
            } else {
                String domain = url.replaceAll("https?://", "").replaceAll("www\\.", "").split("/")[0];
                return domain;
            }
        } catch (Exception e) {
            return "알 수 없음";
        }
    }

    /**
     * 가격 문자열을 Integer로 파싱합니다.
     */
    private Integer parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return null;
        }
        
        try {
            // 숫자가 아닌 문자 제거 (콤마, 통화 기호 등)
            String numericPrice = priceStr.replaceAll("[^0-9]", "");
            return numericPrice.isEmpty() ? null : Integer.parseInt(numericPrice);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse price: {}", priceStr, e);
            return null;
        }
    }

    /**
     * 호텔 ID 문자열을 Long으로 파싱합니다.
     */
    private Long parseHotelId(String hotelIdStr) {
        if (hotelIdStr == null || hotelIdStr.isEmpty()) {
            return null;
        }
        
        try {
            return Long.parseLong(hotelIdStr);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse hotel ID: {}", hotelIdStr, e);
            return null;
        }
    }

    /**
     * 스크래핑 어트랙션 리스트를 도메인 모델로 매핑합니다.
     */
    private List<Attraction> mapAttractions(List<ScrapingAttraction> scrapingAttractions) {
        if (scrapingAttractions == null) {
            return null;
        }
        
        return scrapingAttractions.stream()
                .map(this::mapAttraction)
                .collect(Collectors.toList());
    }

    /**
     * 스크래핑 어트랙션을 도메인 모델로 매핑합니다.
     */
    private Attraction mapAttraction(ScrapingAttraction scrapingAttraction) {
        return Attraction.builder()
                .name(scrapingAttraction.getName())
                .type(scrapingAttraction.getType())
                .latitude(scrapingAttraction.getLatitude())
                .longitude(scrapingAttraction.getLongitude())
                .distance(scrapingAttraction.getDistance())
                .build();
    }

    /**
     * 스크래핑 교통수단 리스트를 도메인 모델로 매핑합니다.
     */
    private List<Transportation> mapTransportations(List<ScrapingTransportation> scrapingTransportations) {
        if (scrapingTransportations == null) {
            return null;
        }
        
        return scrapingTransportations.stream()
                .map(this::mapTransportation)
                .collect(Collectors.toList());
    }

    /**
     * 스크래핑 교통수단을 도메인 모델로 매핑합니다.
     */
    private Transportation mapTransportation(ScrapingTransportation scrapingTransportation) {
        return Transportation.builder()
                .name(scrapingTransportation.getName())
                .type(scrapingTransportation.getType())
                .latitude(scrapingTransportation.getLatitude())
                .longitude(scrapingTransportation.getLongitude())
                .distance(scrapingTransportation.getDistance())
                .build();
    }

    /**
     * 스크래핑 편의시설 리스트를 도메인 모델로 매핑합니다.
     */
    private List<Amenity> mapAmenities(List<ScrapingAmenity> scrapingAmenities) {
        if (scrapingAmenities == null) {
            return null;
        }
        
        return scrapingAmenities.stream()
                .map(this::mapAmenity)
                .collect(Collectors.toList());
    }

    /**
     * 스크래핑 편의시설을 도메인 모델로 매핑합니다.
     */
    private Amenity mapAmenity(ScrapingAmenity scrapingAmenity) {
        return Amenity.builder()
                .type(scrapingAmenity.getType())
                .available(scrapingAmenity.isAvailable())
                .description(scrapingAmenity.getDescription())
                .build();
    }

    /**
     * 체크인 시간을 매핑합니다.
     */
    private CheckTime mapCheckInTime(ScrapingCheckTime scrapingCheckTime) {
        if (scrapingCheckTime == null) {
            return null;
        }
        
        return CheckTime.builder()
                .checkInTimeFrom(scrapingCheckTime.getCheckInTimeFrom())
                .checkInTimeTo(scrapingCheckTime.getCheckInTimeTo())
                .build();
    }

    /**
     * 체크아웃 시간을 매핑합니다.
     */
    private CheckTime mapCheckOutTime(ScrapingCheckTime scrapingCheckTime) {
        if (scrapingCheckTime == null) {
            return null;
        }
        
        return CheckTime.builder()
                .checkInTimeFrom(scrapingCheckTime.getCheckOutTimeFrom())
                .checkInTimeTo(scrapingCheckTime.getCheckOutTimeTo())
                .build();
    }
}