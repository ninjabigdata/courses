package com.tastyfood.order.service.config.jms;

import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JMSConfig {

    public static final String ORDER_VALIDATOR_QUEUE = "order-validator-queue";
    public static final String PAYMENT_QUEUE = "payment-queue";
    public static final String DEALLOCATE_PAYMENT_INVENTORY_QUEUE = "deallocate-payment-inventory-queue";
    public static final String INVENTORY_UPDATE_QUEUE = "inventory-update-queue";
    public static final String DEALLOCATE_DELIVERY_INVENTORY_QUEUE = "deallocate-delivery-inventory-queue";

    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        return converter;
    }

}
