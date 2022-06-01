package com.tastyfood.order.service.mappers;

import com.tastyfood.order.service.dto.CustomerOrderDTO;
import com.tastyfood.order.service.dto.RestaurantOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RestaurantOrderMapper {

    RestaurantOrderMapper RESTAURANT_ORDER_MAPPER = Mappers.getMapper(RestaurantOrderMapper.class);

    RestaurantOrderDTO toRestaurantOrderDTO(CustomerOrderDTO customerOrderDTO);

}
