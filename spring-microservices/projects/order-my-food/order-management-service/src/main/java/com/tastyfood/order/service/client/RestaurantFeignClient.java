package com.tastyfood.order.service.client;

import com.tastyfood.order.service.dto.RestaurantOrderDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "restaurant-search-service")
@LoadBalancerClient(name = "restaurant-search-service")
public interface RestaurantFeignClient {

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "inventoryFallback")
    @PostMapping(value = "/inventory/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    String validateOrder(@RequestBody RestaurantOrderDTO order);

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "inventoryFallback")
    @PostMapping(value = "/inventory/deallocate-payment-order", consumes = MediaType.APPLICATION_JSON_VALUE)
    String deallocatePaymentInventory(@RequestBody RestaurantOrderDTO order);

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "inventoryFallback")
    @PostMapping(value = "/inventory/allocate-delivery-order", consumes = MediaType.APPLICATION_JSON_VALUE)
    String allocateDeliveryInventory(@RequestBody RestaurantOrderDTO order);

    @CircuitBreaker(name = "restaurantService", fallbackMethod = "inventoryFallback")
    @PostMapping(value = "/inventory/deallocate-delivery-order", consumes = MediaType.APPLICATION_JSON_VALUE)
    String deallocateDeliveryInventory(@RequestBody RestaurantOrderDTO order);

    default String inventoryFallback(RestaurantOrderDTO order, Exception exception) {
        return "error";
    }
}
