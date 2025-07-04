package com.yapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SentryConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 