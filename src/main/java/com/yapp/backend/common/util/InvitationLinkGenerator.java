package com.yapp.backend.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 초대 링크 생성 유틸리티 클래스
 * UUID 기반으로 고유한 초대 링크를 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationLinkGenerator {

    /**
     * 고유한 초대 링크를 생성합니다.
     */
    public String generateUniqueInvitationUrl() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}