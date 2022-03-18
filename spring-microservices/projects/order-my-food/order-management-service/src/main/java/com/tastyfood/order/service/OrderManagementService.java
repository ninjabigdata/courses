package com.tastyfood.order.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class OrderManagementService {

    public static void main(String[] args) {
        SpringApplication.run(OrderManagementService.class, args);
    }

}
