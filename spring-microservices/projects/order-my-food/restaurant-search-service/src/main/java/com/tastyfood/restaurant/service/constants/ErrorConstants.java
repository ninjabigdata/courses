package com.tastyfood.restaurant.service.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorConstants {

    public static final String ORDER_NO_RESTAURANT_FOUND = "No restaurant found. Please check the order.";
    public static final String ORDER_ITEM_UNRESERVED = "Failed to order. Please contact support.";
    public static final String ORDER_AMOUNT_MISMATCH = "Paid amount does not match the ordered amount. Please check and reorder.";
    public static final String ORDER_ITEMS_UNAVAILABLE = "Invalid Items ordered / Invalid Restaurant. Please check the order.";
    public static final String ORDER_EXCEPTION = "Exception while validating the order";
    public static final String INVENTORY_DEALLOCATE_PAID = "Exception while deallocating quantity blocked for payment";
    public static final String INVENTORY_ALLOCATE_FOR_DELIVERY = "Exception while allocating quantity for delivery";
    public static final String INVENTORY_DEALLOCATE_DELIVERY_ITEMS = "Exception while deallocating quantity blocked for delivery";

}
