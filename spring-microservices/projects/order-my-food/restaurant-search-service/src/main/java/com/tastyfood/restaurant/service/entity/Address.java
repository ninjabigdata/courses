package com.tastyfood.restaurant.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
