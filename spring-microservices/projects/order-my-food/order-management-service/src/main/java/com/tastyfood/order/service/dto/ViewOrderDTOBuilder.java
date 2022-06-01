package com.tastyfood.order.service.dto;

import com.tastyfood.order.service.domain.OrderState;

import java.util.List;

public class ViewOrderDTOBuilder {
    private Long restaurantId;
    private Long orderId;
    private CustomerDTO customer;
    private List<OrderItemDTO> items;
    private PaymentDTO payment;
    private OrderState orderStatus;

    public ViewOrderDTOBuilder setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public ViewOrderDTOBuilder setOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public ViewOrderDTOBuilder setCustomer(CustomerDTO customer) {
        this.customer = customer;
        return this;
    }

    public ViewOrderDTOBuilder setItems(List<OrderItemDTO> items) {
        this.items = items;
        return this;
    }

    public ViewOrderDTOBuilder setPayment(PaymentDTO payment) {
        this.payment = payment;
        return this;
    }

    public ViewOrderDTOBuilder setOrderStatus(OrderState orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public ViewOrderDTO createViewOrderDTO() {
        return new ViewOrderDTO(restaurantId, orderId, customer, items, payment, orderStatus);
    }
}