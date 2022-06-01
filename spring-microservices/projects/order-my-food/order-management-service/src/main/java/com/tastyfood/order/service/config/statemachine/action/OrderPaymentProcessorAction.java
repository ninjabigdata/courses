package com.tastyfood.order.service.config.statemachine.action;

import com.tastyfood.order.service.config.jms.JMSConfig;
import com.tastyfood.order.service.config.statemachine.OrderManagement;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.entity.Order;
import com.tastyfood.order.service.entity.PaymentStatus;
import com.tastyfood.order.service.repository.OrderRepository;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderPaymentProcessorAction implements Action<OrderState, OrderEvent> {

    private final JmsTemplate jmsTemplate;
    private final OrderRepository orderRepository;

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        Long orderId = (Long) context.getMessageHeader(OrderManagement.SM_HEADER_ORDER_ID);

        try {
            log.debug("Order id is {}", orderId);

            Message response = jmsTemplate.sendAndReceive(JMSConfig.PAYMENT_QUEUE,
                    session -> session.createObjectMessage(orderId));

            OrderEvent eventToBeTriggered;
            Order order = orderRepository.getById(orderId);

            if (ResponseProcessorUtil.isSuccess(response)) {
                eventToBeTriggered = OrderEvent.PAYMENT_SUCCESS;
                order.setStatus(OrderState.PAID);
                order.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
            } else {
                eventToBeTriggered = OrderEvent.PAYMENT_FAILED;
                order.setStatus(OrderState.PAYMENT_FAILED);
                order.getPayment().setPaymentStatus(PaymentStatus.FAILED);
                order.setErrorMessage(ResponseProcessorUtil.getErrorMessage(response));
            }

            orderRepository.save(order);

            log.info("Updated order is {}", order);

            context.getStateMachine().sendEvent(
                    Mono.just(MessageBuilder.withPayload(eventToBeTriggered)
                            .setHeader(OrderManagement.SM_HEADER_ORDER_ID, orderId)
                            .build())
            ).subscribe();
        } catch (RuntimeException exception) {
            log.error("Exception while processing payment for orderId - {}",
                    orderId, exception);

            context.getStateMachine().sendEvent(
                    Mono.just(MessageBuilder.withPayload(OrderEvent.PAYMENT_FAILED)
                            .setHeader(OrderManagement.SM_HEADER_ORDER_ID, orderId)
                            .build())
            ).subscribe();
        }
    }
}
