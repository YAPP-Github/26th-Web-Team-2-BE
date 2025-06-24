package com.yapp.backend.exception;

import io.sentry.Sentry;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(1)
@ControllerAdvice
public class GlobalExceptionHandler {
	private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(status);
		problemDetail.setTitle(title);
		problemDetail.setDetail(detail);
		return problemDetail;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleAllExceptions(Exception e) {
		Sentry.captureException(e);
		String message = (e.getMessage() != null && !e.getMessage().isEmpty())
			? e.getMessage()
			: "서버 오류가 발생했습니다.";
		ProblemDetail problemDetail = createProblemDetail(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"Internal Server Error",
			message
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new StandardResponse<>("error", problemDetail));
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleCustomException(CustomException e) {
		Sentry.captureException(e);
		String message = (e.getMessage() != null && !e.getMessage().isEmpty())
			? e.getMessage()
			: "커스텀 오류가 발생했습니다.";
		ProblemDetail problemDetail = createProblemDetail(
			HttpStatus.BAD_REQUEST,
			e.getTitle(),
			message
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new StandardResponse<>("error", problemDetail));
	}
} 