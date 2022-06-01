package com.tastyfood.restaurant.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO implements Serializable {

    private final static Long serialVersionUID = 1L;

    private Double amount;

}
