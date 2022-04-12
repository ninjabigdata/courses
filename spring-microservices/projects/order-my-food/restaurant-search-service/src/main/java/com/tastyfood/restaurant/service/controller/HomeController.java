package com.tastyfood.restaurant.service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant-search")
@Slf4j
public class HomeController {

    @GetMapping
    public String home() {
        log.info("From HomeController");

        return "Restaurant Search Service";
    }

}