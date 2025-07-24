package com.yapp.backend.controller;

import com.yapp.backend.controller.docs.OauthDocs;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OauthController implements OauthDocs {

    /**
     * kakao 로그인 인가 페이지로 리다이렉트합니다.
     *
     * @param response
     * @throws IOException
     */
    @Override
    @GetMapping("/kakao")
    public void redirectToKakaoAuthorization(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/kakao");
    }
}
