package com.yapp.backend.filter.dto;

import com.yapp.backend.service.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialUserInfo {
    private String provider;
    private String socialId;
    private String email;
    private String nickname;
    private String profileImage;

    public User toModel() {
        return User.builder()
                .id(null)
                .provider(provider)
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .createdAt(null)
                .deletedAt(null)
                .build();
    }
}