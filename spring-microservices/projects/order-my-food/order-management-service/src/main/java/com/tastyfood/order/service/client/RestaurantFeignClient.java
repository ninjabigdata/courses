package com.tastyfood.order.service.client;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "restaurant-search-service")
@LoadBalancerClient(name = "restaurant-search-service")
public interface RestaurantFeignClient {

    @GetMapping("/restaurant-search")
    public String getService();

}
