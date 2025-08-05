package com.yapp.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * OAuth 보안 관련 설정을 관리하는 Properties 클래스
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.security")
public class OAuthSecurityProperties {
    
    /**
     * OAuth 리다이렉트에서 허용되는 도메인 화이트리스트
     */
    private List<String> allowedDomains;
}