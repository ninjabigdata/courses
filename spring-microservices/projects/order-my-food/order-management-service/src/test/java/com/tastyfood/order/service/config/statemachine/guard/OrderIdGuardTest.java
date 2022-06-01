package com.tastyfood.order.service.config.statemachine.guard;

import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateContext;

import static org.junit.jupiter.api.Assertions.*;
import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.ORDER;
import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.ORDER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderIdGuardTest {

    @InjectMocks
    private OrderIdGuard guard;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private StateContext<OrderState, OrderEvent> context;

    @Test
    void evaluateSuccess() {
        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doReturn(true).when(orderRepository).existsById(ORDER_ID);

        assertTrue(guard.evaluate(context));

        verify(orderRepository).existsById(ORDER_ID);
    }

    @Test
    void evaluateFail() {
        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doReturn(false).when(orderRepository).existsById(ORDER_ID);

        assertFalse(guard.evaluate(context));

        verify(orderRepository).existsById(ORDER_ID);
    }


}