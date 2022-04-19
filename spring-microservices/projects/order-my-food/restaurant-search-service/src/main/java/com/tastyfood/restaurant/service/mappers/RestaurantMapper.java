package com.tastyfood.restaurant.service.mappers;

import com.tastyfood.restaurant.service.dto.RestaurantDTO;
import com.tastyfood.restaurant.service.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RestaurantMapper {

    RestaurantMapper RESTAURANT_MAPPER = Mappers.getMapper(RestaurantMapper.class);

    RestaurantDTO toDTO(Restaurant restaurant);

    Restaurant fromDTO(RestaurantDTO restaurantDTO);

}
