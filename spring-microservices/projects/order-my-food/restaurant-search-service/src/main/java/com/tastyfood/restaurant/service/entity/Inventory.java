package com.tastyfood.restaurant.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "quantity_available")
    private Integer quantityAvailable;

    @Column(nullable = false, name = "quantity_blocked_for_payment")
    private Integer quantityBlockedForPayment;

    @Column(nullable = false, name = "quantity_blocked_for_delivery")
    private Integer quantityBlockedForDelivery;

    @OneToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false, updatable = false)
    private Item item;

}
