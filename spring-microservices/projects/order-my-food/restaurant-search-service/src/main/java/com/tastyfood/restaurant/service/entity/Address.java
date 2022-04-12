package com.tastyfood.restaurant.service.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@Builder
public class Address {

    @Column(name = "address_first_line", nullable = false)
    private String addressFirstLine;

    @Column(name = "address_second_line", nullable = false)
    private String addressSecondLine;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String city;

}
