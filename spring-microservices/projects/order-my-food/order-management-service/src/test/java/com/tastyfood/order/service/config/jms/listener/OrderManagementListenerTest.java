package com.tastyfood.order.service.config.jms.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyfood.order.service.client.RestaurantFeignClient;
import com.tastyfood.order.service.dto.ResponseCode;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.dto.RestaurantOrderDTO;
import com.tastyfood.order.service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderManagementListenerTest {

    @InjectMocks
    private OrderManagementListener listener;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private RestaurantFeignClient restaurant;
    @Mock
    private PaymentService paymentService;
    @Mock
    private MessageHeaders messageHeaders;
    @Mock
    private Message message;
    @Mock
    private Destination destination;
    @Mock
    private RestaurantOrderDTO order;
    private static final String SUCCESS_RESPONSE = "success";

    @Test
    void listenOrderValidatorQueue() throws JMSException {
        doReturn(SUCCESS_RESPONSE).when(restaurant).validateOrder(order);
        doReturn(destination).when(message).getJMSReplyTo();

        listener.listenOrderValidatorQueue(order, messageHeaders, message);

        verify(restaurant).validateOrder(order);
        verify(message).getJMSReplyTo();
        verify(jmsTemplate).convertAndSend(destination, SUCCESS_RESPONSE);
    }

    @Test
    void listenOrderPaymentQueue() throws JMSException, JsonProcessingException {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .responseCode(ResponseCode.SUCCESS)
                .response(SUCCESS_RESPONSE)
                .build();
        Long orderId = 1L;

        doReturn(responseDTO).when(paymentService).processPayment(orderId);
        doReturn(destination).when(message).getJMSReplyTo();
        doReturn(SUCCESS_RESPONSE).when(objectMapper).writeValueAsString(responseDTO);

        listener.listenOrderPaymentQueue(orderId, messageHeaders, message);

        verify(paymentService).processPayment(orderId);
        verify(message).getJMSReplyTo();
        verify(jmsTemplate).convertAndSend(any(Destination.class), any(String.class));
    }

    @Test
    void listenDeallocatePaymentInventoryQueue() throws JMSException {
        doReturn(SUCCESS_RESPONSE).when(restaurant).deallocatePaymentInventory(order);
        doReturn(destination).when(message).getJMSReplyTo();

        listener.listenDeallocatePaymentInventoryQueue(order, messageHeaders, message);

        verify(restaurant).deallocatePaymentInventory(order);
        verify(message).getJMSReplyTo();
        verify(jmsTemplate).convertAndSend(destination, SUCCESS_RESPONSE);
    }

    @Test
    void listenInventoryUpdateQueue() throws JMSException {
        doReturn(SUCCESS_RESPONSE).when(restaurant).allocateDeliveryInventory(order);
        doReturn(destination).when(message).getJMSReplyTo();

        listener.listenInventoryUpdateQueue(order, messageHeaders, message);

        verify(restaurant).allocateDeliveryInventory(order);
        verify(message).getJMSReplyTo();
        verify(jmsTemplate).convertAndSend(destination, SUCCESS_RESPONSE);
    }

    @Test
    void listenDeallocateDeliveryInventoryQueue() throws JMSException {
        doReturn(SUCCESS_RESPONSE).when(restaurant).deallocateDeliveryInventory(order);
        doReturn(destination).when(message).getJMSReplyTo();

        listener.listenDeallocateDeliveryInventoryQueue(order, messageHeaders, message);

        verify(restaurant).deallocateDeliveryInventory(order);
        verify(message).getJMSReplyTo();
        verify(jmsTemplate).convertAndSend(destination, SUCCESS_RESPONSE);
    }
}