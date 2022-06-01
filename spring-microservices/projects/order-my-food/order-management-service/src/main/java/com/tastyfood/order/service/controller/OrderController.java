package com.tastyfood.order.service.controller;

import com.tastyfood.order.service.dto.CustomerOrderDTO;
import com.tastyfood.order.service.dto.ResponseDTO;
import com.tastyfood.order.service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public String createOrder(@RequestBody CustomerOrderDTO customerOrderDTO) {
        log.debug("Inputs to createOrder is - {}", customerOrderDTO);

        String response = orderService.processOrder(customerOrderDTO);

        log.debug("Response from createOrder for order - {} is - {}",
                customerOrderDTO, response);

        return response;
    }

    @PutMapping("/{orderId}/{mobile-number}")
    public String updateOrder(@PathVariable("orderId") Long orderId,
                              @PathVariable("mobile-number") String mobileNumber) {
        log.debug("Inputs to updateOrder is - orderId - {} and mobile number - {}",
                orderId, mobileNumber);

        String response = orderService.updateOrder(orderId, mobileNumber);

        log.debug("Response from updateOrder for orderId - {} and mobile number - {} is - {}",
                orderId, mobileNumber, response);

        return response;
    }

    @GetMapping("/{orderId}")
    public ResponseDTO viewOrder(@PathVariable("orderId") Long orderId) {
        log.debug("Inputs to viewOrder is - orderId - {} ", orderId);

        ResponseDTO response = orderService.viewOrder(orderId);

        log.debug("Response from viewOrder for orderId - {} is - {}",
                orderId, response);

        return response;
    }

    @GetMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        log.debug("Inputs to cancelOrder is - orderId - {} ", orderId);

        String response = orderService.cancelOrder(orderId);

        log.debug("Response from cancelOrder for orderId - {} is - {}",
                orderId, response);

        return response;
    }

}
