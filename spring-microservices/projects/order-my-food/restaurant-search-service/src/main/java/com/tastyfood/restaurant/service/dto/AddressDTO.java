package com.tastyfood.restaurant.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressDTO {

    private String addressFirstLine;
    private String addressSecondLine;
    private String area;
    private String city;
}
