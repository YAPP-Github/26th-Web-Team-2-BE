package com.yapp.backend.common.exception;

import io.sentry.Sentry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.common.exception.oauth.KakaoOAuthException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

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
				message);
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
	public ResponseEntity<StandardResponse<ProblemDetail>> handleValidationException(
			MethodArgumentNotValidException e) {
		log.warn("Validation exception occurred", e);
		Sentry.captureException(e);

		String errorMessage = e.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.joining(", "));

		ProblemDetail problemDetail = createProblemDetail(
				HttpStatus.BAD_REQUEST,
				"Validation Failed",
				errorMessage);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleConstraintViolationException(
			ConstraintViolationException e) {
		log.warn("Constraint violation exception occurred", e);
		Sentry.captureException(e);

		String errorMessage = e.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining(", "));

		ProblemDetail problemDetail = createProblemDetail(
				HttpStatus.BAD_REQUEST,
				"Validation Failed",
				errorMessage);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(TripBoardCreationException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleTripBoardCreationException(
			TripBoardCreationException e) {
		log.warn("Trip board creation exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(TripBoardParticipantLimitExceededException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleTripBoardParticipantLimitExceededException(
			TripBoardParticipantLimitExceededException e) {
		log.warn("Trip board participant limit exceeded exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(TripBoardDeleteException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleTripBoardDeleteException(
			TripBoardDeleteException e) {
		log.error("Trip board delete exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(DuplicateInvitationUrlException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleDuplicateInvitationUrlException(
			DuplicateInvitationUrlException e) {
		log.error("Duplicate invitation URL exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(InvalidDestinationException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleInvalidDestinationException(
			InvalidDestinationException e) {
		log.warn("Invalid destination exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(InvalidTravelPeriodException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleInvalidTravelPeriodException(
			InvalidTravelPeriodException e) {
		log.warn("Invalid travel period exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(InvalidPagingParameterException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleInvalidPagingParameterException(
			InvalidPagingParameterException e) {
		log.warn("Invalid paging parameter exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(KakaoOAuthException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleKakaoOAuthException(KakaoOAuthException e) {
		log.warn("Kakao OAuth Exception: {} - Kakao ErrorCode: {}", e.getMessage(), e.getKakaoErrorCode(), e);
		Sentry.captureException(e);

		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);

		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	@ExceptionHandler(UserAuthorizationException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleUserAuthorizationException(
			UserAuthorizationException e) {

		// 요청 정보 추출
		String requestUri;
		String requestMethod;
		String clientIp;
		String userAgent;

		try {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (attributes != null) {
				HttpServletRequest request = attributes.getRequest();
				requestUri = request.getRequestURI();
				requestMethod = request.getMethod();
				clientIp = getClientIpAddress(request);
				userAgent = request.getHeader("User-Agent");
			} else {
				requestUri = "unknown";
				requestMethod = "unknown";
				clientIp = "unknown";
				userAgent = "unknown";
			}
		} catch (Exception ex) {
			log.debug("요청 정보 추출 중 오류 발생", ex);
			requestUri = "unknown";
			requestMethod = "unknown";
			clientIp = "unknown";
			userAgent = "unknown";
		}

		// final 변수로 복사 (람다에서 사용하기 위해)
		final String finalRequestUri = requestUri;
		final String finalRequestMethod = requestMethod;
		final String finalClientIp = clientIp;
		final String finalUserAgent = userAgent;

		// 상세한 보안 이벤트 로깅
		log.warn("SECURITY_ALERT - 권한 없는 접근 차단 - URI: {}, 메서드: {}, IP: {}, UserAgent: {}, 오류: {}",
				finalRequestUri, finalRequestMethod, finalClientIp, finalUserAgent, e.getMessage());

		// 기존 로깅도 유지
		log.warn("사용자 권한 검증 실패: {}", e.getMessage(), e);

		// Sentry에 추가 컨텍스트와 함께 전송
		Sentry.withScope(scope -> {
			scope.setTag("error_type", "authorization_failure");
			scope.setTag("request_uri", finalRequestUri);
			scope.setTag("request_method", finalRequestMethod);
			scope.setTag("client_ip", finalClientIp);
			scope.setExtra("user_agent", finalUserAgent);
			Sentry.captureException(e);
		});

		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	/**
	 * 클라이언트 IP 주소 추출
	 * 프록시나 로드밸런서를 고려하여 실제 클라이언트 IP를 추출합니다.
	 */
	private String getClientIpAddress(HttpServletRequest request) {
		String[] headerNames = {
				"X-Forwarded-For",
				"X-Real-IP",
				"Proxy-Client-IP",
				"WL-Proxy-Client-IP",
				"HTTP_X_FORWARDED_FOR",
				"HTTP_X_FORWARDED",
				"HTTP_X_CLUSTER_CLIENT_IP",
				"HTTP_CLIENT_IP",
				"HTTP_FORWARDED_FOR",
				"HTTP_FORWARDED",
				"HTTP_VIA",
				"REMOTE_ADDR"
		};

		for (String headerName : headerNames) {
			String ip = request.getHeader(headerName);
			if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
				// X-Forwarded-For 헤더는 여러 IP를 포함할 수 있으므로 첫 번째 IP를 사용
				if (ip.contains(",")) {
					ip = ip.split(",")[0].trim();
				}
				return ip;
			}
		}

		return request.getRemoteAddr();
	}

	/**
	 * AOP에서 발생하는 Spring Security 인증 정보 누락 예외 처리
	 */
	@ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleAuthenticationCredentialsNotFoundException(
			AuthenticationCredentialsNotFoundException e) {
		log.warn("Authentication credentials not found in AOP: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = ErrorCode.AUTHENTICATION_CREDENTIALS_NOT_FOUND;
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	/**
	 * AOP에서 발생하는 Spring Security 접근 권한 부족 예외 처리
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleAccessDeniedException(AccessDeniedException e) {
		log.warn("Access denied in AOP: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	/**
	 * 외부 인증 서비스 연동 실패 시 처리
	 */
	@ExceptionHandler(AuthenticationServiceException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleAuthenticationServiceException(
			AuthenticationServiceException e) {
		log.error("AuthenticationServiceException occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = ErrorCode.AUTHENTICATION_SERVICE_ERROR;
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

	/**
	 * 비교표 삭제 예외 처리
	 */
	@ExceptionHandler(ComparisonTableDeleteException.class)
	public ResponseEntity<StandardResponse<ProblemDetail>> handleComparisonTableDeleteException(
			ComparisonTableDeleteException e) {
		log.warn("Comparison table delete exception occurred: {}", e.getMessage(), e);
		Sentry.captureException(e);
		ErrorCode errorCode = e.getErrorCode();
		ProblemDetail problemDetail = createProblemDetail(errorCode);
		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(new StandardResponse<>(ResponseType.ERROR, problemDetail));
	}

}