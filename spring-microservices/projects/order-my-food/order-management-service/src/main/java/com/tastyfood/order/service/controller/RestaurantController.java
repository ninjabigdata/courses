package com.tastyfood.order.service.controller;

import com.tastyfood.order.service.client.RestaurantFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/order-management/restaurant")
@Slf4j
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantFeignClient restaurantFeignClient;

    @GetMapping
    public String getRestaurantGreeting() {
        log.debug("From getRestaurantGreeting");

        String response = restaurantFeignClient.getService();

        log.info("Response from Restaurant is {}", response);

        return response;
    }

}
