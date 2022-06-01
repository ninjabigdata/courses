package com.tastyfood.gateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class GatewayServiceApplicationTest {

    @Test
    void configTest() {
        assertTrue(true);
    }

}