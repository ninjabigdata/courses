package com.tastyfood.order.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-management")
public class HomeController {

    @GetMapping
    public String home() {
        return "Order Management Service";
    }

}
