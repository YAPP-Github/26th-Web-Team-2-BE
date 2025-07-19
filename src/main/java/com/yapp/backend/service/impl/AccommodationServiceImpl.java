package com.yapp.backend.service.impl;

import com.yapp.backend.service.model.Accommodation;
import com.yapp.backend.repository.AccommodationRepository;
import com.yapp.backend.service.model.Amenity;
import com.yapp.backend.service.model.Attraction;
import com.yapp.backend.service.model.CheckTime;
import com.yapp.backend.repository.entity.DistanceInfo;
import com.yapp.backend.service.model.Transportation;
import com.yapp.backend.service.AccommodationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

	private final AccommodationRepository accommodationRepository;

	@Override
	public AccommodationPageResponse findAccommodationsByTableId(Integer tableId, int page, int size) {
		// Base coordinates for Seoul
		double baseLatitude = 37.5665;
		double baseLongitude = 126.9780;

		List<Accommodation> allAccommodations = List.of(
			createMockAccommodation(1L, "신라호텔", "서울 중구 동호로 249", baseLatitude + 0.01, baseLongitude - 0.02),
			createMockAccommodation(2L, "롯데호텔 월드", "서울 송파구 올림픽로 240", baseLatitude - 0.01, baseLongitude + 0.03),
			createMockAccommodation(3L, "파크 하얏트 부산", "부산 해운대구 마린시티1로 51", baseLatitude + 0.02, baseLongitude - 0.01),
			createMockAccommodation(4L, "그랜드 조선 제주", "제주 서귀포시 중문관광로72번길 60", baseLatitude - 0.02, baseLongitude + 0.01),
			createMockAccommodation(5L, "씨마크 호텔", "강원 강릉시 해안로406번길 2", baseLatitude + 0.03, baseLongitude - 0.03),
			createMockAccommodation(6L, "아난티코브", "부산 기장군 기장읍 기장해안로 268-32", baseLatitude - 0.03, baseLongitude + 0.02)
		);

		int startIndex = page * size;
		int endIndex = Math.min(startIndex + size, allAccommodations.size());

		List<AccommodationResponse> accommodationsOnPage = allAccommodations.subList(startIndex, endIndex).stream()
			.map(AccommodationResponse::from)
			.collect(Collectors.toList());

		boolean hasNext = endIndex < allAccommodations.size();

		return AccommodationPageResponse.builder()
			.accommodations(accommodationsOnPage)
			.hasNext(hasNext)
			.build();
	}

	private Accommodation createMockAccommodation(Long id, String name, String address, double latitude, double longitude) {
		return Accommodation.builder()
			.id(id)
			.urlTest("https://example.com/hotel/" + id)
			.siteName("Test Site")
			.memo("메모 " + id)
			.createdAt(LocalDate.now())
			.updatedAt(LocalDate.now())
			.createdBy(1L)
			.tableId(id)
			.accommodationName(name)
			.images(List.of("https://cache.marriott.com/content/dam/marriott-renditions/SELWI/selwi-exterior-8543-hor-feat.jpg?output-quality=70&interpolation=progressive-bilinear&downsize=1920px",
				"https://cache.marriott.com/content/dam/marriott-renditions/SELWI/selwi-lobby-5013-hor-feat.jpg?output-quality=70&interpolation=progressive-bilinear&downsize=1920px"))
			.address(address)
			.latitude(latitude)
			.longitude(longitude)
			.lowestPrice(200000)
			.highestPrice(500000)
			.currency("KRW")
			.reviewScore(4.5)
			.cleanlinessScore(4.8)
			.reviewSummary("최고의 경험!")
			.hotelId(100L + id)
			.nearbyAttractions(createRandomAttractions())
			.nearbyTransportation(createRandomTransportations())
			.amenities(List.of(new Amenity()))
			.checkInTime(new CheckTime("15:00", "23:00"))
			.checkOutTime(new CheckTime("11:00", "12:00"))
			.build();
	}

	private List<Attraction> createRandomAttractions() {
		return List.of(
			Attraction.builder().name("Namsan Tower").type("Landmark").latitude(37.5512).longitude(126.9882).distance("5km").byFoot(new DistanceInfo("60min", "5km")).byCar(new DistanceInfo("15min", "5km")).build(),
			Attraction.builder().name("Gyeongbokgung Palace").type("Historic Site").latitude(37.5796).longitude(126.9770).distance("3km").byFoot(new DistanceInfo("30min", "3km")).byCar(new DistanceInfo("10min", "3km")).build()
		);
	}

	private List<Transportation> createRandomTransportations() {
		return List.of(
			Transportation.builder().name("Seoul Station").type("Train Station").latitude(37.5559).longitude(126.9723).distance("2km").byFoot(new DistanceInfo("20min", "2km")).byCar(new DistanceInfo("5min", "2km")).build(),
			Transportation.builder().name("Gangnam Station").type("Subway Station").latitude(37.4981).longitude(127.0276).distance("10km").byFoot(new DistanceInfo("120min", "10km")).byCar(new DistanceInfo("30min", "10km")).build()
		);
	}
}
