package com.yapp.backend.controller.dto.response;

import com.yapp.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName());
    }
}
