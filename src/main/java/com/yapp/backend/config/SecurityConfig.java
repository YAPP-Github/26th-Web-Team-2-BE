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
                                .cors(cors -> cors.configure(http)) // CORS 설정 추가
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
                                .cors(cors -> cors.configure(http)) // CORS 설정 추가
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
}