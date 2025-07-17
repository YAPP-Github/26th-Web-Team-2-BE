package com.yapp.backend.service;

import com.yapp.backend.domain.User;

public interface UserLoginService {
    User handleOAuthLogin(User user);
}
