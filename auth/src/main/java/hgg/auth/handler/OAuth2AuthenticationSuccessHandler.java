package hgg.auth.handler;

import hgg.auth.adapter.in.oauth.OAuthUserInfoProvider;
import hgg.auth.dto.SocialUserInfo;
import hgg.domain.user.model.User;
import hgg.domain.user.port.in.UserLoginUseCase;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
    private final UserLoginUseCase oauthUseCase;

    public OAuth2AuthenticationSuccessHandler(
            List<OAuthUserInfoProvider> providerList,
            UserLoginUseCase oauthUseCase) {
        // Spring이 "kakao","naver","google" bean 이름 기준으로 자동 주입
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuthUserInfoProvider::getProviderKey, p -> p));
        this.oauthUseCase = oauthUseCase;
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

        // TODO: 3) JWT 발급


    }
}
