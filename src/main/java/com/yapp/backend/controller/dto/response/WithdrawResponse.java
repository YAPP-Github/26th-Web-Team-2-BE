package com.yapp.backend.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WithdrawResponse {
    
    /**
     * 회원탈퇴 성공 여부
     */
    private final boolean withdrawSuccess;
    
}