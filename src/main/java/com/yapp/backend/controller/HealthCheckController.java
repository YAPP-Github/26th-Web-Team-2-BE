package com.yapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yapp.backend.controller.docs.HealthCheckDocs;

@RestController
public class HealthCheckController implements HealthCheckDocs {

    @GetMapping("/api/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("success");
    }

}
