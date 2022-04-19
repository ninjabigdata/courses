package com.tastyfood.restaurant.service.mappers;

import com.tastyfood.restaurant.service.dto.AddressDTO;
import com.tastyfood.restaurant.service.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {

    AddressMapper ADDRESS_MAPPER = Mappers.getMapper(AddressMapper.class);

    AddressDTO toDTO(Address address);

    Address fromDTO(AddressDTO addressDTO);

}
