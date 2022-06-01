package com.tastyfood.order.service.config.statemachine.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.dto.ResponseCode;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPaymentProcessorActionTest {

    @InjectMocks
    private OrderPaymentProcessorAction action;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private StateContext<OrderState, OrderEvent> context;
    @Mock
    private StateMachine<OrderState, OrderEvent> sm;
    @Mock
    private Flux flux;
    @Mock
    private Message message;

    @Test
    void executeSuccess() throws JsonProcessingException, JMSException {
        ResponseDTO response = ResponseDTO.builder()
                .response("success")
                .responseCode(ResponseCode.SUCCESS)
                .build();

        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doReturn(ORDER).when(orderRepository).getById(ORDER_ID);
        doReturn(message).when(jmsTemplate).sendAndReceive(anyString(), any(MessageCreator.class));
        doReturn(new ObjectMapper().writeValueAsString(response)).when(message).getBody(String.class);
        doReturn(sm).when(context).getStateMachine();
        doReturn(flux).when(sm).sendEvent(any(Mono.class));

        action.execute(context);

        verify(jmsTemplate).sendAndReceive(anyString(), any(MessageCreator.class));
        verify(orderRepository).getById(ORDER_ID);
        verify(orderRepository).save(any(Order.class));
        verify(sm).sendEvent(any(Mono.class));
    }

    @Test
    void executeFail() throws JsonProcessingException, JMSException {
        ResponseDTO response = ResponseDTO.builder()
                .response("success")
                .responseCode(ResponseCode.ERROR)
                .build();

        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doReturn(ORDER).when(orderRepository).getById(ORDER_ID);
        doReturn(message).when(jmsTemplate).sendAndReceive(anyString(), any(MessageCreator.class));
        doReturn(new ObjectMapper().writeValueAsString(response)).when(message).getBody(String.class);
        doReturn(sm).when(context).getStateMachine();
        doReturn(flux).when(sm).sendEvent(any(Mono.class));

        action.execute(context);

        verify(jmsTemplate).sendAndReceive(anyString(), any(MessageCreator.class));
        verify(orderRepository).getById(ORDER_ID);
        verify(orderRepository).save(any(Order.class));
        verify(sm).sendEvent(any(Mono.class));
    }

    @Test
    void executeException() {
        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doThrow(new RuntimeException()).when(jmsTemplate).sendAndReceive(anyString(),
                any(MessageCreator.class));
        doReturn(sm).when(context).getStateMachine();
        doReturn(flux).when(sm).sendEvent(any(Mono.class));

        action.execute(context);

        verify(jmsTemplate).sendAndReceive(anyString(), any(MessageCreator.class));
        verify(orderRepository, never()).getById(ORDER_ID);
        verify(orderRepository, never()).save(any(Order.class));
        verify(sm).sendEvent(any(Mono.class));
    }
}