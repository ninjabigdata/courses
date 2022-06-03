package com.spring.reactive.repo;

import com.spring.reactive.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {

    Person mike = new Person(1, "Mike", "A");
    Person gilly = new Person(2, "Gilly", "V");
    Person sachin = new Person(3, "Sachin", "Z");
    Person don = new Person(4, "Don", "P");

    @Override
    public Mono<Person> getById(Integer id) {
        return this.findAll().filter(person -> person.getId().equals(id)).next();
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(mike, gilly, sachin, don);
    }
}
