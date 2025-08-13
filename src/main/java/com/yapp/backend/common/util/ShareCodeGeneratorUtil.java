package com.yapp.backend.common.util;

import java.util.UUID;

/**
 * 공유 코드 생성 유틸리티 클래스
 * 비교표 공유를 위한 안전한 공유 코드를 생성
 * - 하이픈 제거하여 32자리 hex 문자열로 구성
 * - Java 표준 라이브러리 사용으로 안정성 보장
 */
public class ShareCodeGeneratorUtil {

    /**
     * 고유한 공유 코드를 생성합니다.
     * 초대 코드와 동일한 UUID 방식을 사용합니다.
     * 
     * @return 생성된 공유 코드 (예: "f47ac10b58cc4372a5670e02b2c3d479")
     */
    public static String generateUniqueShareCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
