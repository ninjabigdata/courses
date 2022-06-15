package com.spring.reactive.netflux.bootstrap;

import com.spring.reactive.netflux.domain.Movie;
import com.spring.reactive.netflux.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class InitMovies implements CommandLineRunner {

    private final MovieRepository movieRepository;

    @Override
    public void run(String... args) {
        movieRepository.deleteAll()
                .thenMany(
                        Flux.just("Silence of the lambdas", "AEon Flux", "Enter the Mono<Void>",
                                "The Fluxinnator", "Back to the Future", "Meet the Fluxes",
                                "The Lord of Fluxes")
                                .map(Movie::new)
                                .flatMap(movieRepository::save)
                ).subscribe(null, null, () -> movieRepository.findAll().subscribe(System.out::println));
    }
}
