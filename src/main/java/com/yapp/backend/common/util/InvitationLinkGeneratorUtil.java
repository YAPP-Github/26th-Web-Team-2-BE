package com.yapp.backend.common.util;

import java.util.UUID;

/**
 * 초대 링크 생성 유틸리티 클래스
 * UUID 기반으로 고유한 초대 링크를 생성
 */
public class InvitationLinkGeneratorUtil {

    /**
     * 고유한 초대 링크를 생성합니다.
     */
    public static String generateUniqueInvitationUrl() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}