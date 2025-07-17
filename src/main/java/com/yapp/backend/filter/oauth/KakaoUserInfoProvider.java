package com.yapp.backend.filter.oauth;

import com.yapp.backend.filter.dto.SocialUserInfo;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component("kakao")
public class KakaoUserInfoProvider implements OAuthUserInfoProvider{

    @Override
    public String getProviderKey() {
        return "kakao";
    }

    @Override
    public SocialUserInfo extractUserInfo(OAuth2User principal) {
        String id = Objects.requireNonNull(principal.getAttribute("id")).toString();
        Map<String, Object> properties = principal.getAttribute("properties");
        Map<String, Object> account  = principal.getAttribute("kakao_account");
        return new SocialUserInfo(
                getProviderKey(),
                id,
                (String) account.get("email"),
                (String) properties.get("nickname"),
                (String) properties.get("profile_image")
        );
    }
}