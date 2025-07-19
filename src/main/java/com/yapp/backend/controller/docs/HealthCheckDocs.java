package com.yapp.backend.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "HEALTH CHECK API", description = "헬스 체크 테스트 API")
public interface HealthCheckDocs {

    @Operation(summary = "연결 테스트", description = "...")
    ResponseEntity<String> healthCheck();


}
