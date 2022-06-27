package com.microservices.demo.kafka.admin.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class KafkaClientException extends RuntimeException {

    public KafkaClientException(String message) {
        super(message);
    }

    public KafkaClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
