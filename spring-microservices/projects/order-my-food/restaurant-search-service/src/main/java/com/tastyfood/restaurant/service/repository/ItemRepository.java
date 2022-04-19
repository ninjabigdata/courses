package com.tastyfood.restaurant.service.repository;

import com.tastyfood.restaurant.service.entity.Item;
import com.tastyfood.restaurant.service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Set<Item> findAllByRestaurant(Restaurant restaurant);

    Set<Item> findAllByRestaurantId(Long restaurantId);

}
