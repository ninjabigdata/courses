package com.spring.reactive.repo;

import com.spring.reactive.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository  {

    Mono<Person> getById(Integer id);

    Flux<Person> findAll();

}
