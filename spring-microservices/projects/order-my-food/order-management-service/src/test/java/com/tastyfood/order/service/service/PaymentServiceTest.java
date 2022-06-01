package com.tastyfood.order.service.service;

import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.entity.Payment;
import com.tastyfood.order.service.entity.PaymentStatus;
import com.tastyfood.order.service.repository.OrderRepository;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.ORDER_ID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private OrderRepository orderRepository;

    @RepeatedTest(10)
    void processPayment() {
        ResponseDTO response = paymentService.processPayment(ORDER_ID);

        assertNotNull(response);
        assertNotNull(response.getResponseCode());
        assertTrue(Objects.nonNull(response.getResponse())
                || Objects.nonNull(response.getErrorMessage()));
    }

    @Test
    void processRefundValidStatus() {
        Order order = Order.builder()
                .id(ORDER_ID)
                .status(OrderState.ACCEPTED)
                .payment(Payment.builder()
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .build())
                .build();

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        paymentService.processRefund(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository).save(order);
    }

    @Test
    void processRefundInvalidStatus() {
        Order order = Order.builder()
                .id(ORDER_ID)
                .status(OrderState.CANCELLED)
                .payment(Payment.builder()
                        .paymentStatus(PaymentStatus.FAILED)
                        .build())
                .build();

        doReturn(Optional.of(order)).when(orderRepository).findById(ORDER_ID);

        paymentService.processRefund(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(order);
    }

    @Test
    void processRefundNoOrder() {
        Order order = Order.builder()
                .id(ORDER_ID)
                .status(OrderState.CANCELLED)
                .build();

        doReturn(Optional.empty()).when(orderRepository).findById(ORDER_ID);

        paymentService.processRefund(ORDER_ID);

        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(order);
    }
}