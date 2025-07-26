package com.yapp.backend.common.exception;

import io.sentry.Sentry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@Order(1)
@ControllerAdvice
public class GlobalExceptionHandler {
	private static final String DEFAULT_ERROR_MESSAGE = "서버 오류가 발생했습니다.";

	private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(status);
		problemDetail.setTitle(title);
		problemDetail.setDetail(detail);
		return problemDetail;
	}
	private ProblemDetail createProblemDetail(ErrorCode errorCode) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getHttpStatus());
		problemDetail.setTitle(errorCode.getTitle());
		problemDetail.setDetail(errorCode.getMessage());
		return problemDetail;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleAllExceptions(Exception e) {
		log.error("Unhandled exception occurred", e);
		Sentry.captureException(e);
		String message = (e.getMessage() != null && !e.getMessage().isEmpty())
			? e.getMessage()
			: DEFAULT_ERROR_MESSAGE;

		ProblemDetail problemDetail = createProblemDetail(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"Internal Server Error",
			message
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleCustomException(CustomException e) {
		log.warn("Custom exception occurred: {}", e.getErrorCode().name(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleValidationException(MethodArgumentNotValidException e) {
		log.warn("Validation exception occurred", e);
		Sentry.captureException(e);
		
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
			.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
			.collect(Collectors.joining(", "));
		
		ProblemDetail problemDetail = createProblemDetail(
			HttpStatus.BAD_REQUEST,
			"Validation Failed",
			errorMessage
		);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleConstraintViolationException(ConstraintViolationException e) {
		log.warn("Constraint violation exception occurred", e);
		Sentry.captureException(e);
		
		String errorMessage = e.getConstraintViolations().stream()
			.map(ConstraintViolation::getMessage)
			.collect(Collectors.joining(", "));
		
		ProblemDetail problemDetail = createProblemDetail(
			HttpStatus.BAD_REQUEST,
			"Validation Failed",
			errorMessage
		);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}
} 