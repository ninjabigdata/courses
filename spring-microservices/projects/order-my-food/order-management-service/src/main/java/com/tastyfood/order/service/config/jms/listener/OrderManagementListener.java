package com.tastyfood.order.service.config.jms.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyfood.order.service.client.RestaurantFeignClient;
import com.tastyfood.order.service.config.jms.JMSConfig;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.dto.RestaurantOrderDTO;
import com.tastyfood.order.service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import javax.jms.JMSException;
import javax.jms.Message;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderManagementListener {

    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;
    private final RestaurantFeignClient restaurant;
    private final PaymentService paymentService;

    @JmsListener(destination = JMSConfig.ORDER_VALIDATOR_QUEUE)
    public void listenOrderValidatorQueue(@Payload RestaurantOrderDTO restaurantOrderDTO,
                                          @Headers MessageHeaders messageHeaders,
                                          Message message) throws JMSException {
        log.debug("Received message is {}, messageHeaders are {} and payload is {}",
                message, messageHeaders, restaurantOrderDTO);

        String response = restaurant.validateOrder(restaurantOrderDTO);

        log.info("Response from validator is {}", response);

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
    }

    @JmsListener(destination = JMSConfig.PAYMENT_QUEUE)
    public void listenOrderPaymentQueue(@Payload Long orderId,
                                        @Headers MessageHeaders messageHeaders,
                                        Message message) throws JMSException, JsonProcessingException {
        log.debug("Received message is {}, messageHeaders are {} and payload is {}",
                message, messageHeaders, orderId);

        ResponseDTO response = paymentService.processPayment(orderId);

        log.info("Response from paymentProcess for orderId - {} is {}", orderId, response);

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), objectMapper.writeValueAsString(response));
    }

    @JmsListener(destination = JMSConfig.DEALLOCATE_PAYMENT_INVENTORY_QUEUE)
    public void listenDeallocatePaymentInventoryQueue(@Payload RestaurantOrderDTO restaurantOrderDTO,
                                        @Headers MessageHeaders messageHeaders,
                                        Message message) throws JMSException {
        log.debug("Received message is {}, messageHeaders are {} and payload is {}",
                message, messageHeaders, restaurantOrderDTO);

        String response = restaurant.deallocatePaymentInventory(restaurantOrderDTO);

        log.info("Response from deallocatePaymentInventory for order - {} is {}",
                restaurantOrderDTO, response);

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
    }

    @JmsListener(destination = JMSConfig.INVENTORY_UPDATE_QUEUE)
    public void listenInventoryUpdateQueue(@Payload RestaurantOrderDTO restaurantOrderDTO,
                                                      @Headers MessageHeaders messageHeaders,
                                                      Message message) throws JMSException {
        log.debug("Received message is {}, messageHeaders are {} and payload is {}",
                message, messageHeaders, restaurantOrderDTO);

        String response = restaurant.allocateDeliveryInventory(restaurantOrderDTO);

        log.info("Response from inventoryUpdate for order - {} is {}", restaurantOrderDTO, response);

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
    }

    @JmsListener(destination = JMSConfig.DEALLOCATE_DELIVERY_INVENTORY_QUEUE)
    public void listenDeallocateDeliveryInventoryQueue(@Payload RestaurantOrderDTO restaurantOrderDTO,
                                                      @Headers MessageHeaders messageHeaders,
                                                      Message message) throws JMSException {
        log.debug("Received message is {}, messageHeaders are {} and payload is {}",
                message, messageHeaders, restaurantOrderDTO);

        String response = restaurant.deallocateDeliveryInventory(restaurantOrderDTO);

        log.info("Response from deallocateDeliveryInventory for order - {} is {}",
                restaurantOrderDTO, response);

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
    }

}
