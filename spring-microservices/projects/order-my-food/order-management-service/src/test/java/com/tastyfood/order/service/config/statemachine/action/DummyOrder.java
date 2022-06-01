package com.tastyfood.order.service.config.statemachine.action;

import com.tastyfood.order.service.domain.OrderState;
import com.tastyfood.order.service.dto.CustomerDTO;
import com.tastyfood.order.service.dto.CustomerOrderDTO;
import com.tastyfood.order.service.dto.OrderItemDTO;
import com.tastyfood.order.service.dto.PaymentDTO;
import com.tastyfood.order.service.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DummyOrder {

    public static final Long ORDER_ID = 1L;
    public static final Order ORDER = Order.builder()
            .id(ORDER_ID)
            .restaurantId(2L)
            .status(OrderState.CANCELLED)
            .payment(Payment.builder()
                    .amount(309.0)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .id(3L)
                    .build())
            .customer(Customer.builder()
                    .mobileNumber("9874561230")
                    .address("address")
                    .name("ABC")
                    .id(4L)
                    .build())
            .items(Collections.singletonList(Item.builder()
                    .itemId(5L)
                    .quantity(4)
                    .id(5L)
                    .build()))
            .build();

    public static final CustomerOrderDTO CUSTOMER_ORDER = CustomerOrderDTO.builder()
            .restaurantId(2L)
            .customer(CustomerDTO.builder()
                    .mobileNumber("9874561230")
                    .address("address")
                    .name("ABC")
                    .build())
            .payment(PaymentDTO.builder()
                    .amount(309.0)
                    .build())
            .items(Collections.singletonList(OrderItemDTO.builder()
                    .itemId(5L)
                    .quantity(4)
                    .build()))
            .build();

}
