package com.yapp.backend.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private String title;
    public CustomException(String title, String message) {
        super(message);
        this.title = title;
    }
} 