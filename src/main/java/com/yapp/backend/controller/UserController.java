package com.yapp.backend.controller;

import static com.yapp.backend.common.response.ResponseType.SUCCESS;

import com.yapp.backend.common.response.StandardResponse;
import com.yapp.backend.controller.docs.UserDocs;
import com.yapp.backend.controller.dto.response.UserInfoResponse;
import com.yapp.backend.filter.dto.CustomUserDetails;
import com.yapp.backend.service.UserService;
import com.yapp.backend.service.model.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserDocs {

    private final UserService userService;

    /**
     * 사용자 정보 조회 API
     * 현재 로그인한 사용자의 기본 정보를 조회합니다.
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @return 사용자 닉네임과 프로필 이미지 URL
     */
    @Override
    @SecurityRequirement(name = "JWT")
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserInfoResponse>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long userId = userDetails.getUserId();
        User user = userService.getUserById(userId);

        UserInfoResponse response = new UserInfoResponse(
            user.getNickname(),
            user.getProfileImage()
        );
        
        return ResponseEntity.ok(new StandardResponse<>(SUCCESS, response));
    }
}
