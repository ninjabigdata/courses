package guru.springframework.sfgrestbrewery.config;

import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

public class WebClientProperties {

    public static final String BASE_URL = "http://api.springframewok.guru";

    public static final String BEER_V1_PATH = "/api/v1/beer";
    public static final String BEER_V1_GET_BY_ID_PATH = "/api/v1/beer/{uuid}";
    public static final String BEER_V1_UPC_PATH = "/api/v1/beerupc/{upc}";
}
