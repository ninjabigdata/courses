package com.tastyfood.order.service.dto;

import com.tastyfood.order.service.domain.OrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewOrderDTO extends CustomerOrderDTO {

    private final static Long serialVersionUID = 1L;

    private Long orderId;
    private OrderState orderStatus;

    public ViewOrderDTO(Long restaurantId, Long orderId, CustomerDTO customer,
                        List<OrderItemDTO> items, PaymentDTO payment, OrderState orderStatus) {
        super(restaurantId, customer, items, payment);
        this.orderStatus = orderStatus;
        this.orderId = orderId;
    }

}
