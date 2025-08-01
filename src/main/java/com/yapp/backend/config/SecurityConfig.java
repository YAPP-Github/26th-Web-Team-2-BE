package com.yapp.backend.config;

import com.yapp.backend.filter.JwtFilter;
import com.yapp.backend.filter.handler.CustomAuthenticationEntryPoint;
import com.yapp.backend.filter.handler.OAuth2AuthenticationSuccessHandler;
import com.yapp.backend.filter.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
        private final JwtFilter jwtFilter;

        @Value("${swagger.auth.username}")
        private String swaggerUsername;

        @Value("${swagger.auth.password}")
        private String swaggerPassword;

        @Value("${cors.allowed-origins}")
        private String allowedOrigins;

        // Swagger 문서용 Basic Auth 설정
        @Bean
        @Order(1)
        public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
                return http
                                .securityMatcher(
                                                "/swagger/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/v3/api-docs/**")
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // WebConfig의 CORS 설정
                                                                                                   // 사용
                                .authorizeHttpRequests(authorize -> authorize
                                                .anyRequest().authenticated())
                                .httpBasic(httpBasic -> {
                                })
                                .build();
        }

        // 일반 API용 JWT 인증 설정
        @Bean
        @Order(2)
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // WebConfig의 CORS 설정
                                                                                                   // 사용
                                .formLogin(AbstractHttpConfigurer::disable)
                                .addFilterBefore(jwtFilter, OAuth2LoginAuthenticationFilter.class)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(
                                                                "/")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/oauth2/authorization/**",
                                                                "/api/**",
                                                                "/oauth/authorize", // OAuth2 Authorization Endpoint
                                                                "/login/oauth2/**" // OAuth2 code Redirect URI
                                                ).permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oAuth2AuthenticationSuccessHandler))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .build();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                UserDetails user = User.builder()
                                .username(swaggerUsername)
                                .password(passwordEncoder().encode(swaggerPassword))
                                .roles("ADMIN")
                                .build();

                return new InMemoryUserDetailsManager(user);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // CORS 설정을 SecurityConfig에서 정의
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedHeaders(Arrays.asList("*"));

                // 기본 허용 도메인들
                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost",
                                "http://localhost:3000",
                                "https://api.ssok.info",
                                "https://ssok.info",
                                "https://ssok-info.vercel.app"));

                // 환경변수로 추가 도메인 설정 가능
                if (allowedOrigins != null && !allowedOrigins.isEmpty()
                                && !allowedOrigins.equals("http://localhost:3000")) {
                        String[] additionalOrigins = allowedOrigins.split(",");
                        for (String origin : additionalOrigins) {
                                configuration.addAllowedOrigin(origin.trim());
                        }
                }

                configuration.setAllowedOriginPatterns(Arrays.asList(
                                "https://*.ssok.info",
                                "https://*.run.app",
                                "http://*.run.app",
                                "https://ssok-*.vercel.app"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
                return source;
        }
}