package com.tastyfood.restaurant.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantOrderDTO implements Serializable {

    private final static Long serialVersionUID = 1L;

    private Long restaurantId;
    private List<OrderItemDTO> items;
    private PaymentDTO payment;

}
