package com.spring.reactive.repo;

import com.spring.reactive.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

class PersonRepositoryImplTest {

    PersonRepository personRepository;

    @BeforeEach
    void initializeRepo() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);

        Person person = personMono.block();

        System.out.println(person);
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.subscribe(System.out::println);
    }

    @Test
    void getByIdMap() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.map(Person::getFirstName)
                .subscribe(System.out::println);
    }

    @Test
    void findAllFluxBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        Person person = personFlux.blockFirst();

        System.out.println(person);
    }

    @Test
    void findAllFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(System.out :: println);
    }

    @Test
    void findAllFluxToMonoList() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> personsMono = personFlux.collectList();

        personsMono.subscribe(list -> list.forEach(System.out :: println));
    }

    @Test
    void findAllPersonById() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == 3).next();

        personMono.subscribe(System.out :: println);
    }

    @Test
    void findAllPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == 5).next();

        personMono.subscribe(System.out :: println);
    }

    @Test
    void findAllPersonByIdNotFoundWithException() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == 5).single();

        personMono
                .doOnError(throwable -> System.out.println("Error"))
                .onErrorReturn(Person.builder().build())
                .subscribe(System.out :: println);
    }

}