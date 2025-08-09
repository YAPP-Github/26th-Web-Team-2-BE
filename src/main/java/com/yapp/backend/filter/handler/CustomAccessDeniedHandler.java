package com.yapp.backend.filter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.backend.common.exception.ErrorCode;
import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import io.sentry.Sentry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("Access denied for request {}: {}", request.getRequestURI(), accessDeniedException.getMessage());
        Sentry.captureException(accessDeniedException);

        // ErrorCode 사용하여 일관된 응답 생성
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ProblemDetail problemDetail = createProblemDetail(errorCode);
        StandardResponse<ProblemDetail> standardResponse = new StandardResponse<>(ResponseType.ERROR, problemDetail);

        // HTTP 응답 설정
        if (response.isCommitted()) {
            log.debug("Response already committed. Skipping CustomAccessDeniedHandler write.");
            return;
        }
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // JSON 응답 작성
        objectMapper.writeValue(response.getWriter(), standardResponse);
    }

    private ProblemDetail createProblemDetail(ErrorCode errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(errorCode.getHttpStatus());
        problemDetail.setTitle(errorCode.getTitle());
        problemDetail.setDetail(errorCode.getMessage());
        return problemDetail;
    }
}
