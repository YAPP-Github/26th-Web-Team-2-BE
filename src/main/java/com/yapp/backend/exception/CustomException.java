package com.yapp.backend.exception;

public class CustomException extends RuntimeException {
    private String title;
    public CustomException(String title, String message) {
        super(message);
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
} 