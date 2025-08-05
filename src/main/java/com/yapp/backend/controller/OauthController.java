package com.yapp.backend.controller;

import static com.yapp.backend.common.response.ResponseType.*;

import com.yapp.backend.common.response.ResponseType;
import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.OauthDocs;
import com.yapp.backend.controller.dto.request.OauthTokenRequest;
import com.yapp.backend.controller.dto.response.AuthorizeUrlResponse;
import com.yapp.backend.controller.dto.response.OauthTokenResponse;
import com.yapp.backend.service.OauthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OauthController implements OauthDocs {

    private final OauthService oauthService;

    /**
     * 카카오 OAuth 인가 URL을 반환합니다.
     *
     * @return 카카오 OAuth 인가 URL
     */
    @Override
    @GetMapping("/kakao/authorize")
    public ResponseEntity<StandardResponse<AuthorizeUrlResponse>> getKakaoAuthorizeUrl() {
        String authorizeUrl = oauthService.generateAuthorizeUrl("kakao");
        AuthorizeUrlResponse response = new AuthorizeUrlResponse(authorizeUrl);
        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, response));
    }

    /**
     * 카카오 인가 코드를 통해 토큰을 교환하고 JWT를 발급합니다.
     *
     * @param request 인가 코드가 포함된 요청
     * @return JWT 토큰 응답
     */
    @Override
    @PostMapping("/kakao/token")
    public ResponseEntity<StandardResponse<OauthTokenResponse>> exchangeKakaoToken(@Valid @RequestBody OauthTokenRequest request) {
        OauthTokenResponse response = oauthService.exchangeCodeForToken("kakao", request.code());
        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, response));
    }
}