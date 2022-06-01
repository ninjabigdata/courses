package com.tastyfood.order.service.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jms.Message;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResponseProcessorUtilTest {

    @Mock
    private Message message;

    private static final String ERROR_MESSAGE = "Internal error. Please contact support";

    @Test
    void isSuccessNullMessage() {
        assertFalse(ResponseProcessorUtil.isSuccess(null));
    }

    @Test
    void isSuccessInvalidMessage() {
        assertFalse(ResponseProcessorUtil.isSuccess(message));
    }

    @Test
    void getErrorMessageNullMessage() {
        assertEquals(ERROR_MESSAGE, ResponseProcessorUtil.getErrorMessage(null));
    }

    @Test
    void getErrorMessageInvalidMessage() {
        assertEquals(ERROR_MESSAGE, ResponseProcessorUtil.getErrorMessage(message));
    }


}