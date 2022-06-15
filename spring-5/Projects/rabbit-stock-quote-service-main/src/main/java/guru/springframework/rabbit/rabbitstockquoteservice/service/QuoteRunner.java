package guru.springframework.rabbit.rabbitstockquoteservice.service;

import com.rabbitmq.client.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteRunner implements CommandLineRunner {

    private final QuoteGeneratorService generatorService;
    private final QuoteMessageSender messageSender;
    private final Flux<Delivery> deliveryFlux;

    @Override
    public void run(String... args) {
        generatorService.fetchQuoteStream(Duration.ofMillis(100L))
                .take(25)
                .log("Got quote")
                .flatMap(messageSender::sendQuoteMessage)
                .subscribe(result -> log.debug("Sent message to Rabbit"),
                        throwable -> log.error("Got error ", throwable),
                        () -> log.debug("All done"));

        AtomicInteger receivedCount = new AtomicInteger();

        deliveryFlux.subscribe(msg -> log.debug("Received message # {} - {}",
                receivedCount.incrementAndGet(), new String(msg.getBody())));
    }

}
