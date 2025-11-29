package com.yapp.backend.service;

import com.yapp.backend.service.dto.OAuthLoginResult;
import com.yapp.backend.service.model.User;

public interface UserLoginService {
    OAuthLoginResult handleOAuthLogin(User user);
}
