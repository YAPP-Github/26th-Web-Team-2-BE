package com.yapp.common.response;

import lombok.Getter;

@Getter
public class StandardResponse<T> {
    private ResponseType responseType; // "success" or "error"
    private T result;

    public StandardResponse(ResponseType responseType, T result) {
        this.responseType = responseType;
        this.result = result;
    }
} 