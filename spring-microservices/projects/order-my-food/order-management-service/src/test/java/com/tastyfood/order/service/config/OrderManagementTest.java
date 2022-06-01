package com.tastyfood.order.service.config;

import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderManagementTest {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> ordersFactory;

    @Test
    void testOrderManagementStateMachine_DeliverState() {
        StateMachine<OrderState, OrderEvent> stateMachine = ordersFactory.getStateMachine(UUID.randomUUID());

        stateMachine.startReactively().subscribe();

        validateState(OrderState.NEW, stateMachine);

        triggerEventAndValidateState(OrderState.NEW, stateMachine, OrderEvent.VALIDATE);

        stateMachine.stopReactively().subscribe();
    }

    @Test
    void testOrderManagementStateMachine_InvalidOrder() {
        StateMachine<OrderState, OrderEvent> stateMachine = ordersFactory.getStateMachine(UUID.randomUUID());

        stateMachine.startReactively().subscribe();

        validateState(OrderState.NEW, stateMachine);

        triggerEventAndValidateState(OrderState.NEW, stateMachine, OrderEvent.VALIDATE);

        stateMachine.stopReactively().subscribe();
    }

    private void triggerEventAndValidateState(OrderState expectedState, StateMachine<OrderState, OrderEvent> stateMachine, OrderEvent event) {
        Message<OrderEvent> eventMessage = MessageBuilder.withPayload(event).build();
        stateMachine.sendEvent(Mono.just(eventMessage)).subscribe();

        validateState(expectedState, stateMachine);
    }

    private void validateState(OrderState expectedState, StateMachine<OrderState, OrderEvent> stateMachine) {
        assertEquals(expectedState, stateMachine.getState().getId());
    }

}