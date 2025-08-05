package com.yapp.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record KakaoUserInfoResponse(
        Long id,
        
        @JsonProperty("connected_at")
        String connectedAt,
        
        Map<String, Object> properties,
        
        @JsonProperty("kakao_account")
        Map<String, Object> kakaoAccount
) {
    public String getNickname() {
        if (properties == null) return null;
        return (String) properties.get("nickname");
    }
    
    public String getProfileImageUrl() {
        if (properties == null) return null;
        return (String) properties.get("profile_image");
    }
    
    public String getEmail() {
        if (kakaoAccount == null) return null;
        return (String) kakaoAccount.get("email");
    }
}