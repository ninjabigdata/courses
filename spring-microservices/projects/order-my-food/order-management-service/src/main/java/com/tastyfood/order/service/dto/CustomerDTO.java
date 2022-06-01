package com.tastyfood.order.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CustomerDTO implements Serializable {

    private final static Long serialVersionUID = 1L;

    private final String name;
    private final String address;
    private final String mobileNumber;
}
