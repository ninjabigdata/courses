package guru.springframework.sfgrestbrewery.web.functional;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class BeerRouterConfig {

    public static final String BEER_V2_URL = "/api/v2/beer";
    public static final String BEER_V2_URL_ID = "/api/v2/beer/{beerId}";
    public static final String BEER_V2_URL_UPC = "/api/v2/beerUpc";
    public static final String BEER_V2_URL_UPC_PATH = "/api/v2/beerUpc/{upc}";

    @Bean
    public RouterFunction<ServerResponse> beerRoutesV2(BeerHandlerV2 handler) {
        return route().GET(BEER_V2_URL_ID, accept(APPLICATION_JSON), handler::getBeerById)
                .GET(BEER_V2_URL_UPC_PATH, accept(APPLICATION_JSON), handler::getBeerByUpc)
                .POST(BEER_V2_URL, accept(APPLICATION_JSON), handler::saveNewBeer)
                .PUT(BEER_V2_URL_ID, accept(APPLICATION_JSON), handler::updateBeer)
                .build();
    }

}
