package hgg.auth.adapter.in.oauth;

import hgg.auth.dto.SocialUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuthUserInfoProvider {
    String getProviderKey();
    SocialUserInfo extractUserInfo(OAuth2User principal);
}
