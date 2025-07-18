package com.yapp.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title       = "YAPP26 WEB2",
                version     = "v1",
                description = "SSOK 서비스 API 명세"
        )
)
@Configuration
public class SwaggerConfig {

}
