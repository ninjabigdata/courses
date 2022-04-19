package com.tastyfood.restaurant.service.mappers;

import com.tastyfood.restaurant.service.dto.ItemDTO;
import com.tastyfood.restaurant.service.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {

    ItemMapper ITEM_MAPPER = Mappers.getMapper(ItemMapper.class);

    Item fromDTO(ItemDTO itemDTO);

    ItemDTO toDTO(Item item);

}
