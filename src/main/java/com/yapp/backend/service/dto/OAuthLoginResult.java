package com.yapp.backend.service.dto;

import com.yapp.backend.service.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthLoginResult {
    private final User user;
    private final boolean newUser;
}