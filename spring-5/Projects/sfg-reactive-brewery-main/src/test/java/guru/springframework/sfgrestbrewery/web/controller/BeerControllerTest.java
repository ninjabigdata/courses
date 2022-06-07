package guru.springframework.sfgrestbrewery.web.controller;

import guru.springframework.sfgrestbrewery.bootstrap.BeerLoader;
import guru.springframework.sfgrestbrewery.services.BeerService;
import guru.springframework.sfgrestbrewery.web.model.BeerDto;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebFluxTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private BeerService beerService;

    private BeerDto validBeer;

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder()
                .beerName("Test beer")
                .beerStyle("PALE_ALE")
                .upc(BeerLoader.BEER_1_UPC)
                .build();
    }

    @Test
    void getBeerById() {
        UUID uuid = UUID.randomUUID();

        when(beerService.getById(any(), any())).thenReturn(validBeer);

        webTestClient.get()
                .uri("/api/v1/beer/".concat(uuid.toString()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));

    }

    @Test
    void getBeerByUpc() {
        when(beerService.getByUpc(any())).thenReturn(validBeer);

        webTestClient.get()
                .uri("/api/v1/beerUpc/".concat(BeerLoader.BEER_1_UPC))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getBeerName, equalTo(validBeer.getBeerName()));

    }

    @Test
    void listBeers() {
        BeerPagedList beerPagedList = new BeerPagedList(Arrays.asList(validBeer),
                PageRequest.of(1, 1), 1);
        when(beerService.listBeers(any(), any(), any(), any())).thenReturn(beerPagedList);

        webTestClient.get()
                .uri("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerPagedList.class);

    }

}