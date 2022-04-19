package com.tastyfood.gateway.service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/order-management/**")
                        .filters(f -> f.circuitBreaker(cb -> cb.setName("OrderMgmtFallback")
                                .setFallbackUri("forward:/fallback")
                                .addStatusCode("NOT_FOUND")
                                .addStatusCode("INTERNAL_SERVER_ERROR")
                                .setRouteId("OrderMgmtFallback")))
                        .uri("lb://order-management-service"))
                .route(p -> p.path("/restaurant-search/**")
                        .filters(f -> f.circuitBreaker(cb -> cb.setName("RestaurantSearchFallback")
                                .setFallbackUri("forward:/fallback")
                                .addStatusCode("NOT_FOUND")
                                .addStatusCode("INTERNAL_SERVER_ERROR")
                                .setRouteId("RestaurantSearchFallback"))
                                .rewritePath("/restaurant-search/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://restaurant-search-service"))
                .build();
    }

}
