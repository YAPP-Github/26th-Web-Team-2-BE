package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.AccommodationDocs;
import com.yapp.backend.controller.dto.response.AccommodationCountResponse;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.controller.dto.response.AccommodationResponse;
import com.yapp.backend.service.AccommodationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 숙소 도메인 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController implements AccommodationDocs {

	private final AccommodationService accommodationService;

	/**
	 * 숙소 목록 조회 API
	 */
	@Override
	@GetMapping("/search")
	public ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationByTableIdAndUserId(
		@RequestParam Integer tableId,
		@RequestParam Integer page,
		@RequestParam Integer size,
		@RequestParam (required = false) Long userId
	) {
		AccommodationPageResponse response = accommodationService.findAccommodationsByTableId(tableId, page, size, userId);
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
	}

	@Override
	@GetMapping("/count")
	public ResponseEntity<StandardResponse<AccommodationCountResponse>> getAccommodationCountByTableId(
		@RequestParam Long tableId,
		@RequestParam Long userId
	) {
		AccommodationCountResponse accommodationCountResponse = new AccommodationCountResponse(accommodationService.countAccommodationsByTableId(tableId, userId));
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, accommodationCountResponse));
	}
}