package com.tastyfood.restaurant.service.entity;

import com.tastyfood.restaurant.service.constants.Cuisine;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(
            name = "cost_for_two",
            nullable = false
    )
    private Double costForTwo;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Cuisine cuisine;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<Item> menu;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
