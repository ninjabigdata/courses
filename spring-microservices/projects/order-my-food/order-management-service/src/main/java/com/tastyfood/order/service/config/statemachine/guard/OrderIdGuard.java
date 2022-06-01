package com.tastyfood.order.service.config.statemachine.guard;

import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderIdGuard implements Guard<OrderState, OrderEvent> {

    private final OrderRepository orderRepository;

    @Override
    public boolean evaluate(StateContext<OrderState, OrderEvent> context) {
        boolean isValid;
        try {
            Long orderId = (Long) context.getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);

            isValid = orderRepository.existsById(orderId);
        } catch (RuntimeException exception) {
            log.warn("Exception while validating the orderId in context - {}", context);

            isValid = false;
        }
        return isValid;
    }
}
