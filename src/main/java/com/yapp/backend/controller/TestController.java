package com.yapp.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${test.value}")
    private String value;

    @GetMapping("/api/test")
    public String test() {
        return value;
    }

}
