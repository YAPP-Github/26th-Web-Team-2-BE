package com.yapp.backend.domain;

import com.yapp.backend.repository.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private Long id;
    private String name;

    public static User from(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getName());
    }
}
