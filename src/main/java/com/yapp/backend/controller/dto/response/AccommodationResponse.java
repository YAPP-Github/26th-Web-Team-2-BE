package com.yapp.backend.controller.dto.response;

import com.yapp.backend.common.util.SiteLogoUtil;
import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.service.model.Amenity;
import com.yapp.backend.service.model.Attraction;
import com.yapp.backend.service.model.CheckTime;
import com.yapp.backend.service.model.Transportation;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationResponse {
    private static final int MAX_AMENITIES_COUNT = 6;

	private Long id;
	private String url;
	private String siteName;
	private String logoUrl;
	private String memo;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long createdBy;
	private Long tripBoardId;
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

	public static AccommodationResponse from(Accommodation accommodation) {
		return AccommodationResponse.builder()
				.id(accommodation.getId())
				.url(accommodation.getUrl())
				.siteName(accommodation.getSiteName())
				.logoUrl(SiteLogoUtil.getLogoUrl(accommodation.getSiteName()))
				.memo(accommodation.getMemo())
				.createdAt(accommodation.getCreatedAt())
				.updatedAt(accommodation.getUpdatedAt())
				.createdBy(accommodation.getCreatedBy())
				.tripBoardId(accommodation.getTripBoardId())
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
				.amenities(accommodation.getAmenities().subList(0, Math.min(accommodation.getAmenities().size(), MAX_AMENITIES_COUNT))) // 최대 6개까지
				.checkInTime(accommodation.getCheckInTime())
				.checkOutTime(accommodation.getCheckOutTime())
				.build();
	}
}
