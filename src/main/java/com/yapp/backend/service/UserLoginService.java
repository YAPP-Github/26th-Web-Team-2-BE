package com.yapp.backend.service;

import com.yapp.backend.service.model.User;

public interface UserLoginService {
    User handleOAuthLogin(User user);
}
