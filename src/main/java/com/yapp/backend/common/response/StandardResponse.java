package com.yapp.backend.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "API 응답의 표준 형식을 정의하는 클래스")
public class StandardResponse<T> {
    @Schema(description = "응답 유형", example = "SUCCESS")
    private ResponseType responseType; // "success" or "error"
    @Schema(description = "응답 결과 데이터")
    private T result;

    public StandardResponse(ResponseType responseType, T result) {
        this.responseType = responseType;
        this.result = result;
    }
} 