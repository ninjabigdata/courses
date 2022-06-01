package com.tastyfood.gateway.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerController {

    @RequestMapping("/fallback")
    public String fallback() {
        return "Service is unavailable. Please contact support.";
    }

}
