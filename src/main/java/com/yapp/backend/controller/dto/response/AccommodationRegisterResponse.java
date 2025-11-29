package com.yapp.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRegisterResponse {
	private Long accommodationId;
	private String accommodationName;
}
