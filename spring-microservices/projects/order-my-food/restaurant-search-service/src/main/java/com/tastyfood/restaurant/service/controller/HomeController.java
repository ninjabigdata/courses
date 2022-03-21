package com.tastyfood.restaurant.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant-search")
public class HomeController {

    @GetMapping
    public String home() {
        return "Restaurant Search Service";
    }

}