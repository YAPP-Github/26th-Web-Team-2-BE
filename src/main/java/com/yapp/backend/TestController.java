package com.yapp.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${env.test}")
    private String testValue;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(testValue);
    }

}
