package guru.springframework.rabbit.rabbitstockquoteservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteRunner implements CommandLineRunner {

    private final QuoteGeneratorService generatorService;
    private final QuoteMessageSender messageSender;

    @Override
    public void run(String... args) {
        generatorService.fetchQuoteStream(Duration.ofMillis(100L))
                .take(25)
                .log("Got quote")
                .flatMap(messageSender::sendQuoteMessage)
                .subscribe(result -> log.debug("Sent message to Rabbit"), throwable -> log.error("Got error ", throwable), () -> log.debug("All done"));
    }

}
