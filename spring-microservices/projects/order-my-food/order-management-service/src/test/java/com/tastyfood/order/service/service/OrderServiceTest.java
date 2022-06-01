package com.tastyfood.order.service.service;

import com.tastyfood.order.service.MessageConstants;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.dto.CustomerOrderDTO;
import com.tastyfood.order.service.dto.ResponseCode;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.entity.Customer;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.access.StateMachineAccessor;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.ORDER;
import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.ORDER_ID;
import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.CUSTOMER_ORDER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService service;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private StateMachineFactory<OrderState, OrderEvent> smf;
    @Mock
    private StateMachine<OrderState, OrderEvent> sm;
    @Mock
    private Mono<Void> mono;
    @Mock
    private StateMachineAccessor<OrderState, OrderEvent> sma;
    @Mock
    private Flux<StateMachineEventResult<OrderState, OrderEvent>> flux;
    @Mock
    private PaymentService paymentService;
    @Mock
    private Order order;

    private static final String MOBILE_NUMBER = "1231231231";

    @Test
    void processOrderValidOrder() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        doReturn(ORDER).when(orderRepository).save(any(Order.class));
        doReturn(sm).when(smf).getStateMachine(anyString());
        doReturn(mono).when(sm).stopReactively();
        doReturn(sma).when(sm).getStateMachineAccessor();
        doNothing().when(sma).doWithRegion(any());
        doReturn(mono).when(sm).startReactively();
        doReturn(flux).when(sm).sendEvent(any(Mono.class));
        doReturn(order).when(orderRepository).getById(anyLong());
        doAnswer(invocation -> {
            switch (atomicInteger.getAndAdd(1)) {
                case 0:
                    return OrderState.VALID;

                case 1:
                    return OrderState.PAID;

                case 2:
                    return OrderState.INVENTORY_UPDATED;
            }

            return OrderState.NEW;
        }).when(order).getStatus();
        doReturn(ORDER_ID).when(order).getId();

        assertTrue(service.processOrder(CUSTOMER_ORDER).startsWith(MessageConstants.ORDER_PLACED));

        verify(orderRepository).save(any(Order.class));
        verify(smf).getStateMachine(anyString());
        verify(orderRepository, times(4)).getById(anyLong());
    }

    @Test
    void processOrderInvalidOrder() {
        doReturn(ORDER).when(orderRepository).save(any(Order.class));
        doReturn(sm).when(smf).getStateMachine(anyString());
        doReturn(mono).when(sm).stopReactively();
        doReturn(sma).when(sm).getStateMachineAccessor();
        doNothing().when(sma).doWithRegion(any());
        doReturn(mono).when(sm).startReactively();
        doReturn(flux).when(sm).sendEvent(any(Mono.class));
        doReturn(order).when(orderRepository).getById(anyLong());
        doReturn(OrderState.INVALID).when(order).getStatus();
        doReturn(ORDER_ID).when(order).getId();
        doReturn(MessageConstants.FAILED).when(order).getErrorMessage();

        assertEquals(MessageConstants.FAILED, service.processOrder(CUSTOMER_ORDER));

        verify(orderRepository).save(any(Order.class));
        verify(smf).getStateMachine(anyString());
        verify(orderRepository, times(2)).getById(anyLong());
    }

    @Test
    void processOrderException() {
        assertEquals(MessageConstants.CONTACT_ADMIN, service.processOrder(new CustomerOrderDTO()));

        verify(orderRepository, never()).save(any(Order.class));
        verify(smf, never()).getStateMachine(anyString());
        verify(orderRepository, never()).getById(anyLong());
    }

    @Test
    void updateOrderValidOrderId() {
        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(OrderState.ACCEPTED).when(order).getStatus();
        doReturn(new Customer()).when(order).getCustomer();

        String response = service.updateOrder(ORDER_ID, MOBILE_NUMBER);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository).save(order);
        assertEquals(MessageConstants.MOBILE_NUMBER_UPDATED, response);
    }

    @Test
    void updateOrderInvalidOrderId() {
        doReturn(Optional.empty()).when(orderRepository).findById(ORDER_ID);

        String response = service.updateOrder(ORDER_ID, MOBILE_NUMBER);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(order);
        assertEquals(MessageConstants.ORDER_NOT_FOUND, response);
    }

    @Test
    void updateOrderInvalidOrderState() {
        doReturn(Optional.of(ORDER)).when(orderRepository).findById(ORDER_ID);

        String response = service.updateOrder(ORDER_ID, MOBILE_NUMBER);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(ORDER);
        assertEquals(MessageConstants.ORDER_CANNOT_BE_UPDATED, response);
    }

    @Test
    void updateOrderException() {
        doThrow(new RuntimeException()).when(orderRepository).findById(ORDER_ID);

        String response = service.updateOrder(ORDER_ID, MOBILE_NUMBER);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(ORDER);
        assertEquals(MessageConstants.CONTACT_ADMIN, response);
    }

    @Test
    void cancelOrderValidOrderId() {
        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);
        doReturn(OrderState.ACCEPTED).when(order).getStatus();

        String response = service.cancelOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository).save(order);
        verify(paymentService).processRefund(ORDER_ID);
        assertEquals(MessageConstants.ORDER_CANCELLED_WITH_REFUNDED, response);
    }

    @Test
    void cancelOrderInvalidOrderId() {
        doReturn(Optional.empty()).when(orderRepository).findById(ORDER_ID);

        String response = service.cancelOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(order);
        verify(paymentService, never()).processRefund(ORDER_ID);
        assertEquals(MessageConstants.ORDER_NOT_FOUND, response);
    }

    @Test
    void cancelOrderInvalidOrderState() {
        doReturn(Optional.of(ORDER)).when(orderRepository).findById(ORDER_ID);

        String response = service.cancelOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(ORDER);
        verify(paymentService, never()).processRefund(ORDER_ID);
        assertEquals(MessageConstants.ORDER_CANNOT_BE_CANCELLED, response);
    }

    @Test
    void cancelOrderException() {
        doThrow(new RuntimeException()).when(orderRepository).findById(ORDER_ID);

        String response = service.cancelOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(ORDER);
        verify(paymentService, never()).processRefund(ORDER_ID);
        assertEquals(MessageConstants.CONTACT_ADMIN, response);
    }

    @Test
    void viewOrderValidOrderId() {
        doReturn(Optional.of(ORDER)).when(orderRepository).findById(ORDER_ID);

        ResponseDTO response = service.viewOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        assertEquals(ResponseCode.SUCCESS, response.getResponseCode());
        assertNotNull(response.getResponse());
        assertNull(response.getErrorMessage());
    }

    @Test
    void viewOrderInvalidOrderId() {
        doReturn(Optional.empty()).when(orderRepository).findById(ORDER_ID);

        ResponseDTO response = service.viewOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        assertEquals(ResponseCode.ERROR, response.getResponseCode());
        assertNull(response.getResponse());
        assertNotNull(response.getErrorMessage());
    }

    @Test
    void viewOrderException() {
        doReturn(Optional.of(new Order())).when(orderRepository).findById(ORDER_ID);

        ResponseDTO response = service.viewOrder(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        assertEquals(ResponseCode.ERROR, response.getResponseCode());
        assertNull(response.getResponse());
        assertNotNull(response.getErrorMessage());
    }
}