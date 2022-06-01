package com.tastyfood.order.service.config.statemachine.action;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.repository.OrderRepository;
import com.tastyfood.order.service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmAction implements Action<OrderState, OrderEvent> {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    private ScheduledExecutorService scheduleDelivery;

    @PostConstruct
    public void initializeScheduler() {
        scheduleDelivery = Executors.newScheduledThreadPool(5,
                new ThreadFactoryBuilder().setNameFormat("order-delivery-thread-%d").build());
    }

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        Long orderId = (Long) context.getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);

        log.debug("Order id is {}", orderId);

        Order order = orderRepository.getById(orderId);

        log.info("Order retrieved from DB for orderId - {} is {}", orderId, order);

        OrderEvent eventToBeTriggered;

        try {
            scheduleDelivery.schedule(
                    () -> deliverOrder(context.getStateMachine(), orderId), 2, TimeUnit.MINUTES);

            eventToBeTriggered = OrderEvent.ACCEPTED;
            order.setStatus(OrderState.ACCEPTED);
        } catch (RuntimeException exception) {
            log.error("Error while scheduling delivery fo the orderId - {} - ", orderId, exception);

            eventToBeTriggered = OrderEvent.FAILED;
            order.setStatus(OrderState.FAILED);

            paymentService.processRefund(orderId);
        }

        orderRepository.save(order);

        context.getStateMachine().sendEvent(
                Mono.just(MessageBuilder.withPayload(eventToBeTriggered)
                        .setHeader(OrderManagement.SM_HEADER_ORDER_ID, orderId)
                        .build())
        ).subscribe();
    }

    @Transactional
    private void deliverOrder(StateMachine<OrderState, OrderEvent> orderSM, Long orderId) {
        try {
            Order savedOrder = orderRepository.findById(orderId).get();

            if (!OrderState.ACCEPTED.equals(savedOrder.getStatus())) {
                log.info("Invalid order status, so order cannot be delivered");

                return;
            }

            Message<OrderEvent> message = MessageBuilder.withPayload(OrderEvent.DELIVER)
                    .setHeader("orderId", orderId)
                    .build();

            orderSM.sendEvent(Mono.just(message)).subscribe();

            savedOrder.setStatus(OrderState.DELIVERED);

            orderRepository.save(savedOrder);

            log.info("Retrieved order after delivery event is {}", savedOrder);
        } catch (RuntimeException exception) {
            log.error("Error while processing the orderId - {} - ", orderId, exception);

            Order savedOrder = orderRepository.findById(orderId).get();

            savedOrder.setStatus(OrderState.CANCELLED);
            savedOrder.setErrorMessage("Unable to deliver order");

            orderRepository.save(savedOrder);

            paymentService.processRefund(orderId);
        }
    }
}
