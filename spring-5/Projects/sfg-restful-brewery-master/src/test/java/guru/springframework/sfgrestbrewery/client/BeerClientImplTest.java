package guru.springframework.sfgrestbrewery.client;

import guru.springframework.sfgrestbrewery.config.WebClientConfig;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class BeerClientImplTest {

    private BeerClient beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void getBeerById() {
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
    void createBeer() {
    }

    @Test
    void updateBeer() {
    }

    @Test
    void deleteBeerById() {
    }

    @Test
    void getBeerByUPC() {
    }
}