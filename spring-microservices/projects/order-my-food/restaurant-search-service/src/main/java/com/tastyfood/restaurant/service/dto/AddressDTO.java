package com.tastyfood.restaurant.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private String addressFirstLine;
    private String addressSecondLine;
    private String area;
    private String city;
}
