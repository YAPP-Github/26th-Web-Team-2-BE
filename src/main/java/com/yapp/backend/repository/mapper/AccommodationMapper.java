package com.yapp.backend.repository.mapper;

import com.yapp.backend.repository.JpaUserRepository;
import com.yapp.backend.repository.entity.AccommodationEntity;
import com.yapp.backend.repository.entity.UserEntity;
import com.yapp.backend.service.model.Accommodation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mapper utility for converting between AccommodationEntity and Accommodation domain model
 */
@Component
@RequiredArgsConstructor
public class AccommodationMapper {

    private final JpaUserRepository jpaUserRepository;
    /**
     * 숙소 데이터 모델 -> 숙소 도메인 모델
     */
    public Accommodation entityToDomain(AccommodationEntity entity) {
        if (entity == null) {
            return null;
        }

        return Accommodation.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .siteName(entity.getSiteName())
                .memo(entity.getMemo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy().getId())
                .tableId(entity.getTableId())
                .accommodationName(entity.getAccommodationName())
                .images(entity.getImages())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .lowestPrice(entity.getLowestPrice())
                .highestPrice(entity.getHighestPrice())
                .currency(entity.getCurrency())
                .reviewScore(entity.getReviewScore())
                .cleanlinessScore(entity.getCleanlinessScore())
                .reviewSummary(entity.getReviewSummary())
                .hotelId(entity.getHotelId())
                .nearbyAttractions(entity.getNearbyAttractions())
                .nearbyTransportation(entity.getNearbyTransportation())
                .amenities(entity.getAmenities())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .build();
    }

    /**
     * 숙소 도메인 모델 -> 숙소 데이터 모델
     */
    public AccommodationEntity domainToEntity(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }

        UserEntity userProxy = jpaUserRepository.getReferenceById(accommodation.getCreatedBy());

        return AccommodationEntity.builder()
                .id(accommodation.getId())
                .url(accommodation.getUrl())
                .siteName(accommodation.getSiteName())
                .memo(accommodation.getMemo())
                .createdBy(userProxy)
                .accommodationName(accommodation.getAccommodationName())
                .images(accommodation.getImages())
                .address(accommodation.getAddress())
                .latitude(accommodation.getLatitude())
                .longitude(accommodation.getLongitude())
                .lowestPrice(accommodation.getLowestPrice())
                .highestPrice(accommodation.getHighestPrice())
                .currency(accommodation.getCurrency())
                .reviewScore(accommodation.getReviewScore())
                .cleanlinessScore(accommodation.getCleanlinessScore())
                .reviewSummary(accommodation.getReviewSummary())
                .hotelId(accommodation.getHotelId())
                .nearbyAttractions(accommodation.getNearbyAttractions())
                .nearbyTransportation(accommodation.getNearbyTransportation())
                .amenities(accommodation.getAmenities())
                .checkInTime(accommodation.getCheckInTime())
                .checkOutTime(accommodation.getCheckOutTime())
                .build();
    }

    /**
     * 숙소 데이터 모델 생성
     */
    public AccommodationEntity createEntityForRegistration(String url, String memo, Long createdBy, Long tableId) {
        return AccommodationEntity.builder()
                .url(url)
                .memo(memo)
                .createdBy(new UserEntity(createdBy))
                .build();
    }

    /**
     * Converts LocalDateTime to LocalDate, handling null values
     */
    private LocalDate convertToLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }
}