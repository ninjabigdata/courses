package com.tastyfood.gateway.service.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerControllerTest {

    private CircuitBreakerController controller = new CircuitBreakerController();

    @Test
    void fallback() {
        assertEquals("Service is unavailable. Please contact support.",
                controller.fallback());
    }
}