package com.tastyfood.restaurant.service.service;

import com.querydsl.core.BooleanBuilder;
import com.tastyfood.restaurant.service.dto.ItemDTO;
import com.tastyfood.restaurant.service.dto.RestaurantDTO;
import com.tastyfood.restaurant.service.dto.RestaurantSearchDTO;
import com.tastyfood.restaurant.service.entity.QRestaurant;
import com.tastyfood.restaurant.service.entity.Restaurant;
import com.tastyfood.restaurant.service.mappers.ItemMapper;
import com.tastyfood.restaurant.service.mappers.RestaurantMapper;
import com.tastyfood.restaurant.service.repository.ItemRepository;
import com.tastyfood.restaurant.service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ItemRepository itemRepository;

    public List<RestaurantDTO> searchRestaurants(RestaurantSearchDTO restaurantSearchDTO) {
        QRestaurant restaurant = QRestaurant.restaurant;

        BooleanBuilder searchCriteria = new BooleanBuilder();

        if (StringUtils.hasText(restaurantSearchDTO.getName())) {
            searchCriteria.and(restaurant.name.containsIgnoreCase(restaurantSearchDTO.getName()));
        }

        if (Objects.nonNull(restaurantSearchDTO.getCuisine())) {
            searchCriteria.and(restaurant.cuisine.eq(restaurantSearchDTO.getCuisine()));
        }

        if (StringUtils.hasText(restaurantSearchDTO.getLocation())) {
            searchCriteria.and(restaurant.address.area.containsIgnoreCase(restaurantSearchDTO.getLocation()));
        }

        if (Objects.nonNull(restaurantSearchDTO.getBudget())) {
            searchCriteria.and(restaurant.costForTwo.loe(Double.parseDouble(restaurantSearchDTO.getBudget().toString())));
        }

        if (Objects.nonNull(restaurantSearchDTO.getDistance())) {
            searchCriteria.and(restaurant.distance.loe(restaurantSearchDTO.getDistance()));
        }

        List<RestaurantDTO> restaurants = StreamSupport.stream(restaurantRepository.findAll(searchCriteria).spliterator(), false)
                .map(RestaurantMapper.RESTAURANT_MAPPER :: toDTO)
                .collect(Collectors.toList());

        log.debug("Fetched restaurants are {}", restaurants);

        return restaurants;
    }

    public List<ItemDTO> getMenuByRestaurantId(Long restaurantId) {
        log.debug("Fetching menu for restaurantId - {}", restaurantId);

        List<ItemDTO> menu = itemRepository.findAllByRestaurantId(restaurantId)
                .stream().map(ItemMapper.ITEM_MAPPER::toDTO)
                .collect(Collectors.toList());

        log.debug("Menu for restaurantId - {} is {}", restaurantId, menu);

        return menu;
    }

}
