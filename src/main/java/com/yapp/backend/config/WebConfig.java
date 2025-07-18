package com.yapp.backend.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "https://ssok.info",
                        "https://dev.ssok.info",
                        "https://prod.ssok.info"
                )
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowCredentials(true);
    }
}