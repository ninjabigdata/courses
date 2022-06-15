package guru.springframework.sfgrestbrewery.client;

import guru.springframework.sfgrestbrewery.config.WebClientConfig;
import guru.springframework.sfgrestbrewery.web.model.BeerDto;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BeerClientImplTest {

    private BeerClient beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void getBeerById() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        UUID beerId = beerPagedList.getContent().get(0).getId();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId, false);

        BeerDto beerDto = beerDtoMono.block();

        assertEquals(beerId, beerDto.getId());
        assertNull(beerDto.getQuantityOnHand());
    }
    @Test
    void getBeerByIdShowInventoryTrue() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        UUID beerId = beerPagedList.getContent().get(0).getId();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId, true);

        BeerDto beerDto = beerDtoMono.block();

        assertEquals(beerId, beerDto.getId());
        assertNotNull(beerDto.getQuantityOnHand());
    }

    @Test
    void listBeers() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertNotNull(beerPagedList);
        assertTrue(beerPagedList.getContent().size() > 8);
    }

    @Test
    void listBeersPageSize10() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 10, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertNotNull(beerPagedList);
        assertTrue(beerPagedList.getContent().size() == 10);
    }

    @Test
    void listBeersPageSizeNotFound() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(120, 10, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertNotNull(beerPagedList);
        assertTrue(beerPagedList.getContent().size() == 0);
    }

    @Test
    void getBeerByUPC() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        String upc = beerPagedList.getContent().get(0).getUpc();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerByUPC(upc);

        BeerDto beerDto = beerDtoMono.block();

        assertEquals(upc, beerDto.getUpc());
    }

    @Test
    void createBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("Dogfishhead 90 Min IPA")
                .beerStyle("IPA")
                .upc(UUID.randomUUID().toString())
                .price(new BigDecimal("10.99"))
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);

        ResponseEntity responseEntity = responseEntityMono.block();
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void updateBeer() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        BeerDto beerDto = beerPagedList.getContent().get(0);

        BeerDto updatedBeer = BeerDto.builder()
                .beerName("Really good beer")
                .beerStyle(beerDto.getBeerStyle())
                .upc(beerDto.getUpc())
                .price(beerDto.getPrice())
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(beerDto.getId(), updatedBeer);

        ResponseEntity<Void> responseEntity = responseEntityMono.block();

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    void deleteBeerById() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null,
                null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        BeerDto beerDto = beerPagedList.getContent().get(0);

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(beerDto.getId());

        ResponseEntity<Void> responseEntity = responseEntityMono.block();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteBeerByIdNotFound() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());

        assertThrows(WebClientResponseException.class, () -> {
            ResponseEntity<Void> responseEntity = responseEntityMono.block();

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
    }

    @Test
    void deleteBeerByIdException() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());

        ResponseEntity<Void> responseEntity = responseEntityMono
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException) {
                        WebClientResponseException exception = (WebClientResponseException) throwable;

                        return Mono.<ResponseEntity<Void>>just(
                                ResponseEntity.<Void>status(exception.getStatusCode())
                                        .build());
                    } else {
                        throw new RuntimeException(throwable);
                    }
                })
                .block();

        assertThrows(WebClientResponseException.class, () -> {


            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
    }

    @Test
    void functionTestGetBeerById() throws InterruptedException {
        AtomicReference<String> beerName = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        beerClient.listBeers(null, null, null,
                null, null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(beerId -> beerClient.getBeerById(beerId, false))
                .flatMap(mono -> mono)
                .subscribe(beerDto -> {
                    System.out.println(beerDto.getBeerName());

                    beerName.set(beerDto.getBeerName());

                    countDownLatch.countDown();
                });

        countDownLatch.await();

        assertNotNull(beerName.get());
    }

}