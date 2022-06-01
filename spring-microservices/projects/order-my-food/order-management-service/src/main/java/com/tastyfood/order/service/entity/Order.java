package com.tastyfood.order.service.entity;

import com.tastyfood.order.service.domain.OrderState;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "food_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState status;

    @Column(name = "error_message")
    private String errorMessage;

    @OneToOne(mappedBy = "order", optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Item> items;

    @OneToOne(mappedBy = "order", optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Payment payment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
