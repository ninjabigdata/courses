package com.spring.reactive.repo;

import com.spring.reactive.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

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
    void findAll() {
    }
}