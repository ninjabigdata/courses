package com.tastyfood.order.service.config.statemachine.action;

import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.repository.OrderRepository;
import com.tastyfood.order.service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderConfirmActionTest {

    @InjectMocks
    private OrderConfirmAction action;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private StateContext<OrderState, OrderEvent> context;
    @Mock
    private StateMachine<OrderState, OrderEvent> sm;
    @Mock
    private Flux flux;

    private static final Long ORDER_ID = 1l;

    @Test
    void execute_acceptedOrder() {
        Order order = buildOrder();
        action.initializeScheduler();

        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doReturn(order).when(orderRepository).getById(ORDER_ID);
        doReturn(sm).when(context).getStateMachine();
        doReturn(flux).when(sm).sendEvent(any(Mono.class));

        action.execute(context);

        verify(sm).sendEvent(any(Mono.class));
        verify(orderRepository).getById(ORDER_ID);
        verify(orderRepository).save(order);

        assertEquals(OrderState.ACCEPTED, order.getStatus());
    }

    @Test
    void execute_Exception() {
        Order order = buildOrder();

        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doReturn(order).when(orderRepository).getById(ORDER_ID);
        doReturn(sm).when(context).getStateMachine();
        doReturn(flux).when(sm).sendEvent(any(Mono.class));

        action.execute(context);

        verify(sm).sendEvent(any(Mono.class));
        verify(orderRepository).getById(ORDER_ID);
        verify(orderRepository).save(order);
        verify(paymentService).processRefund(ORDER_ID);

        assertEquals(OrderState.FAILED, order.getStatus());
    }

    @Test
    void deliverOrder_ValidStatus() {
        Order order = buildOrder();
        order.setStatus(OrderState.ACCEPTED);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(flux).when(sm).sendEvent(any(Mono.class));

        ReflectionTestUtils.invokeMethod(action, "deliverOrder", sm, ORDER_ID);

        verify(sm).sendEvent(any(Mono.class));
        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository).save(order);
        verify(paymentService, never()).processRefund(ORDER_ID);

        assertEquals(OrderState.DELIVERED, order.getStatus());
    }

    @Test
    void deliverOrder_InvalidStatus() {
        Order order = buildOrder();
        order.setStatus(OrderState.NEW);

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        ReflectionTestUtils.invokeMethod(action, "deliverOrder", sm, ORDER_ID);

        verify(sm, never()).sendEvent(any(Mono.class));
        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(order);
        verify(paymentService, never()).processRefund(ORDER_ID);

        assertNotEquals(OrderState.DELIVERED, order.getStatus());
    }

    @Test
    void deliverOrder_Exception() {
        Order order = buildOrder();

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        ReflectionTestUtils.invokeMethod(action, "deliverOrder", sm, ORDER_ID);

        verify(orderRepository, times(2)).findById(ORDER_ID);
        verify(orderRepository).save(order);
        verify(paymentService).processRefund(ORDER_ID);

        assertEquals(OrderState.CANCELLED, order.getStatus());
    }

    private Order buildOrder() {
        return Order.builder()
                .status(OrderState.ACCEPTED)
                .id(ORDER_ID)
                .errorMessage(null)
                .build();
    }

}