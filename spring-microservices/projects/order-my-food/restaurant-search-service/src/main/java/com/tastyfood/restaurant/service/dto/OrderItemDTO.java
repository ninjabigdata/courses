package com.tastyfood.restaurant.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO implements Serializable {

    private final static Long serialVersionUID = 1L;

    private Long itemId;
    private Integer quantity;

}
