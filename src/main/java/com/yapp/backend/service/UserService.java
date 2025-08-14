package com.yapp.backend.service;

import com.yapp.backend.service.model.User;
import com.yapp.backend.controller.dto.response.WithdrawResponse;

public interface UserService {
    /**
     * 활성 사용자 정보를 조회합니다.
     * 탈퇴된 사용자인 경우 예외를 발생시킵니다.
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 도메인 모델 (활성 사용자만)
     * @throws DeletedUserException 탈퇴된 사용자인 경우
     */
    User getUserById(Long userId);
    
    /**
     * 회원탈퇴 처리 (Soft Delete)
     * @param userId
     * @return
     */
    Boolean withdrawUser(Long userId);
}
