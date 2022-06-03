package com.spring.reactive.netflux.service;

import com.spring.reactive.netflux.domain.Movie;
import com.spring.reactive.netflux.domain.MovieEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {

    Mono<Movie> getMovieById(String id);

    Flux<Movie> getAllMovies();

    Flux<MovieEvent> streamMovieEvents(String id);

}
