package com.tastyfood.restaurant.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;

}
