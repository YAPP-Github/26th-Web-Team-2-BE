package com.yapp.backend.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 사이트명에 따른 로고 URL을 제공하는 유틸리티 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SiteLogoUtil {

    private static final Map<String, String> SITE_LOGO_MAP = new HashMap<>();

    static {
        SITE_LOGO_MAP.put("agoda",
                "https://play-lh.googleusercontent.com/EN4vEdLW-Y2CudJ01SiOsa3XOv5MdlO7uOVAmm-FuE6gDmPZZshcQDu-SuEI1RpTG0g=w600-h300-pc0xffffff-pd");
        SITE_LOGO_MAP.put("booking",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Booking.com_Icon_2022.svg/1200px-Booking.com_Icon_2022.svg.png");
    }

    /**
     * 사이트명에 해당하는 로고 URL을 반환합니다.
     */
    public static String getLogoUrl(String siteName) {
        if (!isSupportedSite(siteName)) {
            return null;
        }
        return SITE_LOGO_MAP.get(siteName.toLowerCase());
    }

    /**
     * 지원하는 사이트인지 확인합니다.
     */
    public static boolean isSupportedSite(String siteName) {
        return siteName != null && SITE_LOGO_MAP.containsKey(siteName.toLowerCase());
    }
}