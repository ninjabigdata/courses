package com.tastyfood.order.service.config.statemachine;

import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
@Slf4j
public class OrderManagement extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    public static final String SM_HEADER_ORDER_ID = "orderId";

    private final Action<OrderState, OrderEvent> orderValidatorAction;
    private final Action<OrderState, OrderEvent> orderPaymentProcessorAction;
    private final Action<OrderState, OrderEvent> orderDeallocatePaymentInventoryAction;
    private final Action<OrderState, OrderEvent> orderInventoryUpdateAction;
    private final Action<OrderState, OrderEvent> orderDeallocateDeliveryInventoryAction;
    private final Action<OrderState, OrderEvent> orderConfirmAction;
    private final Guard<OrderState, OrderEvent> orderIdGuard;

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderState.NEW)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.FAILED)
                .end(OrderState.PAYMENT_FAILED)
                .end(OrderState.INVENTORY_UPDATE_FAILED)
                .end(OrderState.DELIVERED)
                .end(OrderState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions)
            throws Exception {
        try {
            transitions.withExternal().source(OrderState.NEW).target(OrderState.NEW)
                        .event(OrderEvent.VALIDATE).action(orderValidatorAction)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.NEW).target(OrderState.VALID)
                        .event(OrderEvent.VALIDATION_SUCCESS).guard(orderIdGuard)
                    .and().withExternal().source(OrderState.NEW).target(OrderState.INVALID)
                        .event(OrderEvent.VALIDATION_FAILED).guard(orderIdGuard)
                    .and().withExternal().source(OrderState.VALID).target(OrderState.VALID)
                        .event(OrderEvent.PROCESS_PAYMENT).action(orderPaymentProcessorAction)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.VALID).target(OrderState.PAID)
                        .event(OrderEvent.PAYMENT_SUCCESS)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.VALID).target(OrderState.PAYMENT_FAILED)
                        .event(OrderEvent.PAYMENT_FAILED).action(orderDeallocatePaymentInventoryAction)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.PAID).target(OrderState.PAID)
                        .event(OrderEvent.UPDATE_INVENTORY).action(orderInventoryUpdateAction)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.PAID).target(OrderState.INVENTORY_UPDATED)
                        .event(OrderEvent.INVENTORY_UPDATE_SUCCESS).guard(orderIdGuard)
                    .and().withExternal().source(OrderState.PAID).target(OrderState.INVENTORY_UPDATE_FAILED)
                        .event(OrderEvent.INVENTORY_UPDATE_FAILED).action(orderDeallocatePaymentInventoryAction)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.INVENTORY_UPDATED).target(OrderState.INVENTORY_UPDATED)
                        .event(OrderEvent.ACCEPT).action(orderConfirmAction)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.INVENTORY_UPDATED).target(OrderState.ACCEPTED)
                        .event(OrderEvent.ACCEPTED).guard(orderIdGuard)
                    .and().withExternal().source(OrderState.INVENTORY_UPDATED).target(OrderState.FAILED)
                        .event(OrderEvent.FAILED).action(orderDeallocateDeliveryInventoryAction)
                        .guard(orderIdGuard)
                    .and().withExternal().source(OrderState.ACCEPTED).target(OrderState.CANCELLED)
                        .event(OrderEvent.CANCEL).guard(orderIdGuard)
                    .and().withExternal().source(OrderState.ACCEPTED).target(OrderState.DELIVERED)
                        .event(OrderEvent.DELIVER).guard(orderIdGuard);
        } catch (Exception exception) {
            log.error("Exception in OrderManagement", exception);

            throw exception;
        }
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        try {
            StateMachineListenerAdapter<OrderState, OrderEvent>
                    listenerAdapter =
                    new StateMachineListenerAdapter<OrderState, OrderEvent>() {
                        @Override
                        public void stateChanged(State from, State to) {
                            log.info("State Changed from {} to {}", from, to);
                        }
                    };

            config.withConfiguration().listener(listenerAdapter);
        } catch (Exception exception) {
            log.error("Exception in OrderManagement", exception);

            throw exception;
        }
    }
}
