package com.yapp.backend.exception;

import lombok.Getter;

@Getter
public class StandardResponse<T> {
    private String responseType; // "success" or "error"
    private T result;

    public StandardResponse(String responseType, T result) {
        this.responseType = responseType;
        this.result = result;
    }
}