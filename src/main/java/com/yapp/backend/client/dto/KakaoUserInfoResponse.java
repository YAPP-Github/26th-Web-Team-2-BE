package com.yapp.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoUserInfoResponse {
    private Long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    private Map<String, Object> properties;

    @JsonProperty("kakao_account")
    private Map<String, Object> kakaoAccount;

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