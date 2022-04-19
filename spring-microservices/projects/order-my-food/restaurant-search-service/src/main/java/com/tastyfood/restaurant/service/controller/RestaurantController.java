package com.tastyfood.restaurant.service.controller;

import com.tastyfood.restaurant.service.dto.ResponseCode;
import com.tastyfood.restaurant.service.dto.ResponseDTO;
import com.tastyfood.restaurant.service.dto.RestaurantSearchDTO;
import com.tastyfood.restaurant.service.entity.Restaurant;
import com.tastyfood.restaurant.service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("search")
    public ResponseDTO search(@RequestBody RestaurantSearchDTO restaurantSearchDTO) {
        ResponseDTO.ResponseDTOBuilder responseDTO = ResponseDTO.builder();
        if (Objects.isNull(restaurantSearchDTO)
                || (
                        StringUtils.hasText(restaurantSearchDTO.getLocation())
                                && StringUtils.hasText(restaurantSearchDTO.getName())
                                && Objects.isNull(restaurantSearchDTO.getCuisine())
                                && Objects.isNull(restaurantSearchDTO.getBudget())
                                && Objects.isNull(restaurantSearchDTO.getDistance()))) {

            log.warn("No search criteria is provided - {}", restaurantSearchDTO);

            responseDTO.responseCode(ResponseCode.ERROR)
                    .response("Atleast one criterion is required for searching");
        }

        List<Restaurant> restaurants = restaurantService.searchRestaurants(restaurantSearchDTO);

        log.debug("Restaurants matching the search criteria are - {}", restaurants);

        responseDTO.responseCode(ResponseCode.SUCCESS)
                .response(restaurants);

        log.debug("Response from search is {}", responseDTO.build());

        return responseDTO.build();
    }

}
