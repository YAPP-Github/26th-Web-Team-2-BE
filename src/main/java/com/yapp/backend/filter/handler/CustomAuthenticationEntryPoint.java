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
//        String uri = request.getRequestURI();
//        if (uri.equals("/") ||
//                uri.startsWith("/swagger") ||
//                uri.startsWith("/v3/api-docs") ||
//                uri.startsWith("/swagger-ui")) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
