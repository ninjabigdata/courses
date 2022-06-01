package com.tastyfood.order.service.config.jms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JMSConfigTest {

    @Test
    void messageConverter() {
        assertNotNull(new JMSConfig().messageConverter());
    }
}