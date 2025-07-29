package com.yapp.backend.controller;

import com.yapp.backend.common.exception.CustomException;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.AccommodationDocs;
import com.yapp.backend.controller.dto.request.AccommodationRegisterRequest;
import com.yapp.backend.controller.dto.response.AccommodationCountResponse;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationRegisterResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.service.AccommodationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 숙소 도메인 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
@Validated
public class AccommodationController implements AccommodationDocs {

	private final AccommodationService accommodationService;

	/**
	 * 숙소 목록 조회 API
	 * todo sehwan 그룹에 속한 userId 만 조회할 수 있도록 로직 필요
	 */
	@Override
	@GetMapping("/search")
	public ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationByTableIdAndUserId(
			@RequestParam Long tableId,
			@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam(required = false) Long userId,
			@RequestParam(defaultValue = "saved_at_desc") String sort) {

		AccommodationPageResponse response = accommodationService.findAccommodationsByTableId(tableId, page, size,
				userId, sort);
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
	}

	/**
	 * 숙소 개수 조회 API
	 * todo sehwan 그룹에 속한 userId 만 조회할 수 있도록 로직 필요
	 */
	@Override
	@GetMapping("/count")
	public ResponseEntity<StandardResponse<AccommodationCountResponse>> getAccommodationCountByTableId(
			@RequestParam Long tableId,
			@RequestParam(required = false) Long userId) {
		AccommodationCountResponse accommodationCountResponse = new AccommodationCountResponse(
				accommodationService.countAccommodationsByTableId(tableId, userId));
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, accommodationCountResponse));
	}

	/**
	 * 숙소 카드 등록 API
	 * todo sehwan 그룹에 속한 userId 만 등록할 수 있도록 로직 필요
	 */
	@Override
	@PostMapping("/register")
	public ResponseEntity<StandardResponse<AccommodationRegisterResponse>> registerAccommodationCard(
			@RequestBody AccommodationRegisterRequest request) {
		AccommodationRegisterResponse response = accommodationService.registerAccommodationCard(request);
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
	}

	/**
	 * 숙소 단건 조회 API
	 * 특정 숙소 ID로 숙소 정보를 조회합니다.
	 */
	@Override
	@GetMapping("/{accommodationId}")
	public ResponseEntity<StandardResponse<AccommodationResponse>> getAccommodationById(
			@PathVariable Long accommodationId) {
		AccommodationResponse response = accommodationService.findAccommodationById(accommodationId);
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
	}
}