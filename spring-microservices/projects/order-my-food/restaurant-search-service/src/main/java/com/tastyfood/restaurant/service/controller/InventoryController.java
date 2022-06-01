package com.tastyfood.restaurant.service.controller;

import com.tastyfood.restaurant.service.dto.ResponseDTO;
import com.tastyfood.restaurant.service.dto.RestaurantOrderDTO;
import com.tastyfood.restaurant.service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("validate")
    public ResponseDTO validate(@RequestBody RestaurantOrderDTO restaurantOrderDTO) {
        log.debug("Inputs for validate is {}", restaurantOrderDTO);

        ResponseDTO response = inventoryService.validateOrderItemQuantity(restaurantOrderDTO);

        log.info("Status of order validation for order - {} is {}", restaurantOrderDTO, response);

        return response;
    }

    @PostMapping("deallocate-payment-order")
    public ResponseDTO deallocateQuantityBlockedForPayment(@RequestBody RestaurantOrderDTO restaurantOrderDTO) {
        log.debug("Inputs for deallocating quantity blocked for payment is {}", restaurantOrderDTO);

        ResponseDTO response = inventoryService.deallocateQuantityBlockedForPayment(restaurantOrderDTO);

        log.info("Status of order deallocating quantity blocked for payment for order - {} is {}",
                restaurantOrderDTO, response);

        return response;
    }

    @PostMapping("allocate-delivery-order")
    public ResponseDTO allocateQuantityBlockedForDelivery(@RequestBody RestaurantOrderDTO restaurantOrderDTO) {
        log.debug("Inputs for allocating quantity blocked for delivery is {}", restaurantOrderDTO);

        ResponseDTO response = inventoryService.allocateQuantityBlockedForDelivery(restaurantOrderDTO);

        log.info("Status of order allocating quantity blocked for delivery for order - {} is {}",
                restaurantOrderDTO, response);

        return response;
    }

    @PostMapping("deallocate-delivery-order")
    public ResponseDTO deallocateQuantityBlockedForDelivery(@RequestBody RestaurantOrderDTO restaurantOrderDTO) {
        log.debug("Inputs for deallocating quantity blocked for delivery is {}", restaurantOrderDTO);

        ResponseDTO response = inventoryService.deallocateQuantityBlockedForDelivery(restaurantOrderDTO);

        log.info("Status of order deallocating quantity blocked for delivery for order - {} is {}",
                restaurantOrderDTO, response);

        return response;
    }

}
