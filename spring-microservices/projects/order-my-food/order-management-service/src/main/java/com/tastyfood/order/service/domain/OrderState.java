package com.tastyfood.order.service.domain;

public enum OrderState {

    NEW,
    VALID, INVALID,
    PAID, PAYMENT_FAILED,
    INVENTORY_UPDATED, INVENTORY_UPDATE_FAILED,
    ACCEPTED, FAILED,
    DELIVERED,
    CANCELLED

}
