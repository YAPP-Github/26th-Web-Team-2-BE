package com.yapp.backend.filter.handler;

import com.yapp.backend.service.model.User;
import com.yapp.backend.common.util.JwtTokenProvider;
import com.yapp.backend.filter.dto.SocialUserInfo;
import com.yapp.backend.filter.oauth.OAuthUserInfoProvider;
import com.yapp.backend.service.UserLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * OAuth2 Authentication 성공시 실행되는 Handler 입니다.
 * - 각 Social Provider 에서 공통된 사용자 정보를 생성
 * - provider 정보를 통해 User 를 조회하거나 저장
 * - User 정보를 활용해 생성한 JWT를 쿠키에 담아 응답을 전달
 *
 */
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final Map<String, OAuthUserInfoProvider> providers;
    private final UserLoginService oauthUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2AuthenticationSuccessHandler(
            List<OAuthUserInfoProvider> providerList,
            UserLoginService oauthUseCase,
            JwtTokenProvider jwtTokenProvider
        ) {
        // Spring이 "kakao","naver","google" bean 이름 기준으로 자동 주입
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuthUserInfoProvider::getProviderKey, p -> p));
        this.oauthUseCase = oauthUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        // 1) 공통 소셜 정보
        OAuth2User principal = oauthToken.getPrincipal();
        SocialUserInfo info = providers.get(provider).extractUserInfo(principal);

        // 2) 사용자 저장 or 조회
        User user = oauthUseCase.handleOAuthLogin(info.toModel());

        // 3) JWT 발급
        ResponseCookie accessTokenCookie = jwtTokenProvider.generateAccessTokenCookie(user.getId());
        ResponseCookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(user.getId());
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());


        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"redirect\":\"https://ssok.info/oauth2/callback\"}");

//        String origin = request.getHeader(HttpHeaders.ORIGIN);
//        if (origin == null) {
//            origin = request.getHeader(HttpHeaders.REFERER);
//        }
//
//        getRedirectStrategy().sendRedirect(request, response, origin + "/oauth2/callback");
    }
}