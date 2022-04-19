package com.tastyfood.restaurant.service.repository;

import com.tastyfood.restaurant.service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>,
        QuerydslPredicateExecutor<Restaurant> {
}
