package com.tastyfood.restaurant.service.dto;

import com.tastyfood.restaurant.service.constants.Cuisine;
import com.tastyfood.restaurant.service.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDTO {

    private Long id;
    private String name;
    private Double costForTwo;
    private Double distance;
    private Cuisine cuisine;
    private AddressDTO address;

}
