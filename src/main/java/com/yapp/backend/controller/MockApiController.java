package com.yapp.backend.controller;

import com.yapp.backend.exception.CustomException;
import com.yapp.backend.exception.ErrorCode;
import com.yapp.common.response.StandardResponse;
import com.yapp.common.response.ResponseType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/mock")
@Slf4j
public class MockApiController {
	// todo 응답, 예외, 에러 처리 테스트용 & 애플리케이션 로깅 Mock API로 제거 필요
	private static final Logger logger = LoggerFactory.getLogger(MockApiController.class);

	// 정상 응답
	@GetMapping("/success")
	public ResponseEntity<StandardResponse<String>> success() {
		logger.info("정상 응답 API 호출");
		return ResponseEntity.ok(new StandardResponse<>(ResponseType.SUCCESS, "정상 응답입니다."));
	}

	// CustomException 발생
	@GetMapping("/custom-error")
	public ResponseEntity<StandardResponse<String>> customError() {
		logger.error("커스텀 에러 발생");
		throw new CustomException(ErrorCode.CUSTOM_ERROR);
	}

	// 일반 Exception 발생
	@GetMapping("/exception-error")
	public ResponseEntity<StandardResponse<String>> exceptionError() {
		logger.error("일반 예외 발생");
		throw new RuntimeException("이것은 일반 예외입니다.");
	}
} 