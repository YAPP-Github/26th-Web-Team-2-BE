package com.yapp.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedHeaders("*")
                .allowedOrigins(
                        "http://localhost",
                        "http://localhost:3000",
                        "https://api.ssok.info",
                        "https://ssok.info",
                        "https://ssok-info.vercel.app"
                )
                .allowedOriginPatterns(
                        "https://*.ssok.info",
                        "https://*.run.app",
                        "http://*.run.app",
                        "https://ssok-*.vercel.app"
                )
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS", "PATCH")
                .allowCredentials(true);
    }
}