package com.tastyfood.order.service.config.statemachine.action;

import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.dto.RestaurantOrderDTO;
import com.tastyfood.order.service.repository.OrderRepository;
import com.tastyfood.order.service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static com.tastyfood.order.service.config.statemachine.action.DummyOrder.*;

@ExtendWith(MockitoExtension.class)
class OrderDeallocateDeliveryInventoryActionTest {

    @InjectMocks
    private OrderDeallocateDeliveryInventoryAction action;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private StateContext<OrderState, OrderEvent> context;

    @Test
    void execute() {
        doReturn(ORDER_ID).when(context).getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);
        doReturn(ORDER).when(orderRepository).getById(ORDER_ID);

        action.execute(context);

        verify(jmsTemplate).convertAndSend(anyString(), any(RestaurantOrderDTO.class));
        verify(orderRepository).getById(ORDER_ID);
        verify(paymentService).processRefund(ORDER_ID);
    }
}