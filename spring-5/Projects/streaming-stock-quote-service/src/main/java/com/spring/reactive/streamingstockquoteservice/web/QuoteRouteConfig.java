package com.spring.reactive.streamingstockquoteservice.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_NDJSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class QuoteRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> quoteRoutes(QuoteHandler handler) {
        return route().GET("/quotes", accept(APPLICATION_JSON), handler::fetchQuotes)
                .GET("/quotes", accept(APPLICATION_NDJSON), handler::streamQuotes)
                .build();
    }

}
