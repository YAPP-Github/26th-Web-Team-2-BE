package com.yapp.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title       = "YAPP26 WEB2",
                version     = "v1",
                description = "SSOK 서비스 API 명세"
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Access Token을 Authorization 헤더로 전송"
)
@Configuration
public class SwaggerConfig {
}
