package com.yapp.backend.filter.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        String uri = request.getRequestURI();
        // Swagger 요청이면 401이 아니라 그냥 허용 or Forbidden 응답
        if (uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Swagger access forbidden");
            return;
        }

        // 그 외 요청에 대해서는 기존처럼 401 반환
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
