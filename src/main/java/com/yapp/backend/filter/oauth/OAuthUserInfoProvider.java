package com.yapp.backend.filter.oauth;

import com.yapp.backend.filter.dto.SocialUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuthUserInfoProvider {
    String getProviderKey();
    SocialUserInfo extractUserInfo(OAuth2User principal);
}