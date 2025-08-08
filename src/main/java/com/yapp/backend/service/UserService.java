package com.yapp.backend.service;

import com.yapp.backend.service.model.User;
import com.yapp.backend.controller.dto.response.WithdrawResponse;

public interface UserService {
    User getUserById(Long id);
    
    /**
     * 회원탈퇴 처리 (Soft Delete)
     * @param userId
     * @return
     */
    Boolean withdrawUser(Long userId);
}
