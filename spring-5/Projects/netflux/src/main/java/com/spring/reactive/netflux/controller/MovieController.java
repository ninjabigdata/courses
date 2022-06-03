package com.spring.reactive.netflux.controller;

import com.spring.reactive.netflux.domain.Movie;
import com.spring.reactive.netflux.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable String id) {
        return service.getMovieById(id);
    }

    @GetMapping
    public Flux<Movie> getMovies() {
        return service.getAllMovies();
    }

}
