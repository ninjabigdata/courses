package com.tastyfood.restaurant.service.controller;

import com.tastyfood.restaurant.service.dto.*;
import com.tastyfood.restaurant.service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
        } else {
            List<RestaurantDTO> restaurants = restaurantService.searchRestaurants(restaurantSearchDTO);

            log.debug("Restaurants matching the search criteria are - {}", restaurants);

            responseDTO.responseCode(ResponseCode.SUCCESS)
                    .response(restaurants);
        }

        log.debug("Response from search is {}", responseDTO.build());

        return responseDTO.build();
    }

    @GetMapping("{restaurantId}/menu")
    public ResponseDTO getMenu(@PathVariable("restaurantId") Long restaurantId) {
        ResponseDTO.ResponseDTOBuilder response = ResponseDTO.builder();

        if (Objects.isNull(restaurantId)) {

            log.warn("Restaurant Id is required");

            response.responseCode(ResponseCode.ERROR)
                    .response("Restaurant Id is required");
        } else {
            List<ItemDTO> menu = restaurantService.getMenuByRestaurantId(restaurantId);

            log.debug("Menu is - {}", menu);

            response.responseCode(ResponseCode.SUCCESS)
                    .response(menu);
        }

        log.debug("Response from menu is {}", response.build());

        return response.build();
    }

}
