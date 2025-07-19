package com.yapp.backend.controller;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.AccommodationDocs;
import com.yapp.backend.controller.dto.response.AccommodationPageResponse;
import com.yapp.backend.service.AccommodationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController implements AccommodationDocs {

	private final AccommodationService accommodationService;

	@Override
	@GetMapping("/search")
	public ResponseEntity<StandardResponse<AccommodationPageResponse>> getAccommodationsByTitle(
		@RequestParam String title,
		@RequestParam Integer page,
		@RequestParam Integer size
	) {
		AccommodationPageResponse response = accommodationService.findAccommodationsByTitle(title, page, size);
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, response));
	}
}