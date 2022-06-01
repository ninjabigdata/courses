package com.tastyfood.order.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstants {

    public static final String FAILED = "failed";
    public static final String CONTACT_ADMIN = "Please contact Admin";
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_ID = "orderId";
    public static final String MOBILE_NUMBER_UPDATED = "Mobile number updated";
    public static final String ORDER_CANNOT_BE_UPDATED = "Order cannot be updated.";
    public static final String ORDER_CANCELLED_WITH_REFUNDED = "Order has been cancelled. Payment will be refunded.";
    public static final String ORDER_CANNOT_BE_CANCELLED = "Order cannot be cancelled.";
    public static final String ORDER_PLACED = "Order placed. Your order id is - ";
}
