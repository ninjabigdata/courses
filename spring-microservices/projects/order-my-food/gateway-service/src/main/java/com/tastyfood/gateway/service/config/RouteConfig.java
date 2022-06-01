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
                                .addStatusCode("METHOD_NOT_ALLOWED")
                                .setRouteId("OrderMgmtFallback"))
                                .rewritePath("/order-management/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://order-management-service"))
                .route(p -> p.path("/restaurant-search/**")
                        .filters(f -> f.circuitBreaker(cb -> cb.setName("RestaurantSearchFallback")
                                .setFallbackUri("forward:/fallback")
                                .addStatusCode("NOT_FOUND")
                                .addStatusCode("INTERNAL_SERVER_ERROR")
                                .addStatusCode("METHOD_NOT_ALLOWED")
                                .setRouteId("RestaurantSearchFallback"))
                                .rewritePath("/restaurant-search/(?<segment>.*)", "/$\\{segment}"))
                        .uri("lb://restaurant-search-service"))
                .build();
    }

}
