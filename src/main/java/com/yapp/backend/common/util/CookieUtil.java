package com.yapp.backend.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

    /**
     * HttpServletRequest 에서 이름이 cookieName 인 쿠키 값을 꺼내 반환.
     * 없으면 null.
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    /**
     * 쿠키를 무효화하는 ResponseCookie를 생성합니다.
     * 
     * @param cookieName 무효화할 쿠키 이름
     * @return 무효화된 ResponseCookie
     */
    public static ResponseCookie createInvalidatedCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .path("/")
                .maxAge(0) // 즉시 만료
                .httpOnly(true)
                .secure(false) // 개발환경용, 프로덕션에서는 true로 설정
                .sameSite("Lax")
                .build();
    }

}