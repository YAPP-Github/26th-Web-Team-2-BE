package com.yapp.backend.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBoardCreateRequest {
    @NotBlank(message = "여행 보드 제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String boardName;

    @NotBlank(message = "여행지는 필수입니다.")
    @Size(max = 20, message = "여행지는 20자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "여행지는 한글, 영문, 공백만 허용됩니다.")
    private String destination;

    @NotNull(message = "출발일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "도착일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}