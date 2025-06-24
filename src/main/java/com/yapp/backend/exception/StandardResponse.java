package com.yapp.backend.exception;

import org.springframework.http.ProblemDetail;

import lombok.Getter;

@Getter
public class StandardResponse<T> {
    private String resultType; // "success" or "error"
    private T result;

    public StandardResponse(String resultType, T result) {
        this.resultType = resultType;
        this.result = result;
    }
}