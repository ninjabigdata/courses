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
import com.tastyfood.order.service.utils.ResponseProcessorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.jms.Message;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderInventoryUpdateAction implements Action<OrderState, OrderEvent> {

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

        Message response = jmsTemplate.sendAndReceive(JMSConfig.INVENTORY_UPDATE_QUEUE,
                session -> session.createObjectMessage(restaurantOrder));

        OrderEvent eventToBeTriggered;

        if (ResponseProcessorUtil.isSuccess(response)) {
            eventToBeTriggered = OrderEvent.INVENTORY_UPDATE_SUCCESS;
            order.setStatus(OrderState.INVENTORY_UPDATED);
        } else {
            eventToBeTriggered = OrderEvent.INVENTORY_UPDATE_FAILED;
            order.setStatus(OrderState.INVENTORY_UPDATE_FAILED);
            order.setErrorMessage(ResponseProcessorUtil.getErrorMessage(response));

            paymentService.processRefund(orderId);
        }

        orderRepository.save(order);

        context.getStateMachine().sendEvent(
                Mono.just(MessageBuilder.withPayload(eventToBeTriggered)
                        .setHeader(OrderManagement.SM_HEADER_ORDER_ID, orderId)
                        .build())
        ).subscribe();
    }
}
