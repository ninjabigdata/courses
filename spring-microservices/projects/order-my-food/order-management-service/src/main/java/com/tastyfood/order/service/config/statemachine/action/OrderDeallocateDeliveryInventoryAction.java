package com.tastyfood.order.service.config.statemachine.action;

import com.tastyfood.order.service.config.jms.JMSConfig;
import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.dto.OrderItemDTO;
import com.tastyfood.order.service.dto.PaymentDTO;
import com.tastyfood.order.service.dto.RestaurantOrderDTO;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.repository.OrderRepository;
import com.tastyfood.order.service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDeallocateDeliveryInventoryAction implements Action<OrderState, OrderEvent> {

    private final JmsTemplate jmsTemplate;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        Long orderId = (Long) context.getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);

        log.debug("Order id is {}", orderId);

        Order order = orderRepository.getById(orderId);

        log.info("Order retrieved from DB for orderId - {} is {}", orderId, order);

        RestaurantOrderDTO restaurantOrder = RestaurantOrderDTO.builder().restaurantId(order.getRestaurantId())
                .items(order.getItems().stream().map(item -> OrderItemDTO.builder()
                                .itemId(item.getItemId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .payment(PaymentDTO.builder()
                        .amount(order.getPayment().getAmount())
                        .build())
                .build();

        jmsTemplate.convertAndSend(JMSConfig.DEALLOCATE_DELIVERY_INVENTORY_QUEUE, restaurantOrder);

        paymentService.processRefund(orderId);
    }
}
