package com.tastyfood.restaurant.service.dto;

import com.tastyfood.restaurant.service.constants.Cuisine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantSearchDTO {

    private String location;
    private Double distance;
    private Cuisine cuisine;
    private Integer budget;
    private String name;

}
