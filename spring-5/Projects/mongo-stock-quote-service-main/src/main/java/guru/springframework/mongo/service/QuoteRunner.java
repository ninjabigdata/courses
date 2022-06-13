package guru.springframework.mongo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteRunner implements CommandLineRunner {

    private final QuoteHistoryService quoteHistoryService;
    private final QuoteGeneratorService quoteGeneratorService;

    @Override
    public void run(String... args) {
        quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100L))
                .take(50)
                .log("Get Quote: ")
                .flatMap(quoteHistoryService::saveQuoteToMongo)
                .subscribe(savedQuote -> log.debug("Saved Quote is {}", savedQuote),
                        throwable -> {
                    // Handle error here
                    log.error("Error here ", throwable);
                },
                        () -> log.debug("All Done.")
                );

    }

}
