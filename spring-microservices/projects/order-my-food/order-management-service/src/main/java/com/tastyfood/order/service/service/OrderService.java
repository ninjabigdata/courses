package com.tastyfood.order.service.service;

import com.tastyfood.order.service.MessageConstants;
import com.tastyfood.order.service.domain.OrderEvent;
import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.dto.*;
import com.tastyfood.order.service.entity.*;
import com.tastyfood.order.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final StateMachineFactory<OrderState, OrderEvent> smf;
    private final PaymentService paymentService;

    public String processOrder(CustomerOrderDTO customerOrderDTO) {
        log.debug("Processing order - {}", customerOrderDTO);

        String response = MessageConstants.FAILED;

        try {
            Order order = Order.builder()
                    .restaurantId(customerOrderDTO.getRestaurantId())
                    .status(OrderState.NEW)
                    .build();
            order.setCustomer(Customer.builder()
                    .name(customerOrderDTO.getCustomer().getName())
                    .address(customerOrderDTO.getCustomer().getAddress())
                    .mobileNumber(customerOrderDTO.getCustomer().getMobileNumber())
                    .order(order)
                    .build());
            order.setItems(customerOrderDTO.getItems().stream()
                    .map(orderItem -> Item.builder()
                            .itemId(orderItem.getItemId())
                            .quantity(orderItem.getQuantity())
                            .order(order)
                            .build())
                    .collect(Collectors.toList()));

            order.setPayment(Payment.builder()
                    .amount(customerOrderDTO.getPayment().getAmount())
                    .paymentStatus(PaymentStatus.NEW)
                    .order(order)
                    .build());

            Order savedOrder = orderRepository.save(order);

            log.debug("Saved order is {}", savedOrder);

            StateMachine<OrderState, OrderEvent> orderSM =
                    getOrderStateMachineByOrder(savedOrder);

            Message<OrderEvent> message = MessageBuilder.withPayload(OrderEvent.VALIDATE)
                    .setHeader(MessageConstants.ORDER_ID, savedOrder.getId())
                    .build();

            orderSM.sendEvent(Mono.just(message)).subscribe();

            savedOrder = orderRepository.getById(savedOrder.getId());

            log.info("Retrieved order after validating event is {}", savedOrder);

            if (OrderState.VALID.equals(savedOrder.getStatus())) {
                message = MessageBuilder.withPayload(OrderEvent.PROCESS_PAYMENT)
                        .setHeader(MessageConstants.ORDER_ID, savedOrder.getId())
                        .build();

                orderSM.sendEvent(Mono.just(message)).subscribe();

                savedOrder = orderRepository.getById(savedOrder.getId());

                log.info("Retrieved order after payment process event is {}", savedOrder);

                if (OrderState.PAID.equals(savedOrder.getStatus())) {
                    message = MessageBuilder.withPayload(OrderEvent.UPDATE_INVENTORY)
                            .setHeader(MessageConstants.ORDER_ID, savedOrder.getId())
                            .build();

                    orderSM.sendEvent(Mono.just(message)).subscribe();

                    savedOrder = orderRepository.getById(savedOrder.getId());

                    log.info("Retrieved order after update inventory process event is {}", savedOrder);

                    if (OrderState.INVENTORY_UPDATED.equals(savedOrder.getStatus())) {
                        message = MessageBuilder.withPayload(OrderEvent.ACCEPT)
                                .setHeader(MessageConstants.ORDER_ID, savedOrder.getId())
                                .build();

                        orderSM.sendEvent(Mono.just(message)).subscribe();

                        savedOrder = orderRepository.getById(savedOrder.getId());

                        log.info("Retrieved order after update inventory process event is {}", savedOrder);

                        response = MessageConstants.ORDER_PLACED.concat(savedOrder.getId().toString());
                    }
                }
            }

            if (MessageConstants.FAILED.equals(response)) {
                response = orderRepository.getById(savedOrder.getId()).getErrorMessage();
            }
        } catch (Exception exception) {
            log.error("Error while processing the order - {} - ", customerOrderDTO, exception);

            response = MessageConstants.CONTACT_ADMIN;
        }

        log.debug("Response from processOrder is {}", response);

        return response;
    }

    public String updateOrder(Long orderId, String mobileNumber) {
        String response;

        try {
            Order order = orderRepository.findById(orderId).orElse(null);

            if (Objects.isNull(order)) {
                response = MessageConstants.ORDER_NOT_FOUND;
            } else {
                if (OrderState.ACCEPTED.equals(order.getStatus())) {
                    order.getCustomer().setMobileNumber(mobileNumber);

                    orderRepository.save(order);

                    response = MessageConstants.MOBILE_NUMBER_UPDATED;
                } else {
                    response = MessageConstants.ORDER_CANNOT_BE_UPDATED;
                }
            }
        } catch (RuntimeException exception) {
            log.error("Error while updating the orderId - {} with mobile number - {}", orderId,
                    mobileNumber, exception);

            response = MessageConstants.CONTACT_ADMIN;
        }

        log.info("Order update response for OrderId - {} with mobile number - {} is {}",
                orderId, mobileNumber, response);

        return response;
    }

    public String cancelOrder(Long orderId) {
        String response;

        try {
            Order order = orderRepository.findById(orderId).orElse(null);

            if (Objects.isNull(order)) {
                response = MessageConstants.ORDER_NOT_FOUND;
            } else {
                if (OrderState.ACCEPTED.equals(order.getStatus())) {
                    order.setStatus(OrderState.CANCELLED);

                    orderRepository.save(order);

                    response = MessageConstants.ORDER_CANCELLED_WITH_REFUNDED;

                    paymentService.processRefund(orderId);
                } else {
                    response = MessageConstants.ORDER_CANNOT_BE_CANCELLED;
                }
            }
        } catch (RuntimeException exception) {
            log.error("Error while cancelling the orderId - {}", orderId,
                    exception);

            response = MessageConstants.CONTACT_ADMIN;
        }

        log.info("Order cancel response for OrderId - {} is {}",
                orderId, response);

        return response;
    }

    public ResponseDTO viewOrder(Long orderId) {
        ResponseDTO.ResponseDTOBuilder responseBuilder = ResponseDTO.builder();

        try {
            Order order = orderRepository.findById(orderId).orElse(null);

            if (Objects.isNull(order)) {
                responseBuilder.responseCode(ResponseCode.ERROR)
                        .errorMessage(MessageConstants.ORDER_NOT_FOUND);
            } else {
                responseBuilder.responseCode(ResponseCode.SUCCESS)
                        .response(new ViewOrderDTOBuilder()
                                .setOrderId(order.getId())
                                .setRestaurantId(order.getRestaurantId())
                                .setOrderStatus(order.getStatus())
                                .setItems(order.getItems().stream().map(item ->
                                        OrderItemDTO.builder()
                                                .itemId(item.getItemId())
                                                .quantity(item.getQuantity())
                                                .build())
                                        .collect(Collectors.toList()))
                                .setCustomer(CustomerDTO.builder()
                                        .name(order.getCustomer().getName())
                                        .address(order.getCustomer().getAddress())
                                        .mobileNumber(order.getCustomer().getMobileNumber())
                                        .build())
                                .setPayment(PaymentDTO.builder()
                                        .amount(order.getPayment().getAmount())
                                        .build())
                                .createViewOrderDTO());
            }
        } catch (RuntimeException exception) {
            log.error("Error while fetching the orderId - {}", orderId,
                    exception);

            responseBuilder.responseCode(ResponseCode.ERROR)
                    .errorMessage(MessageConstants.CONTACT_ADMIN);
        }

        log.info("Order view response for OrderId - {} is {}",
                orderId, responseBuilder.build());

        return responseBuilder.build();
    }

    private StateMachine<OrderState, OrderEvent> getOrderStateMachineByOrder(Order order) {
        StateMachine<OrderState, OrderEvent> orderSM = smf.getStateMachine(Long.toString(order.getId()));

        orderSM.stopReactively().subscribe();

        orderSM.getStateMachineAccessor().doWithRegion(orderStateMachineAccess ->
                orderStateMachineAccess.resetStateMachineReactively(
                                new DefaultStateMachineContext(order.getStatus(), null, null, null))
                        .subscribe());

        orderSM.startReactively().subscribe();

        return orderSM;
    }

}
