package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRegisterRequest {
	@NotBlank(message = "URL은 필수 입력값입니다.")
	private String url;
	
	@Size(max = 50, message = "메모는 500자를 초과할 수 없습니다.")
	private String memo;
	
	@NotNull(message = "테이블 ID는 필수 입력값입니다.")
	private Long tableId;
	
	@NotNull(message = "사용자 ID는 필수 입력값입니다.")
	private Long userId;
}
