package guru.springframework.rabbit.rabbitstockquoteservice.service;

import guru.springframework.rabbit.rabbitstockquoteservice.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.Receiver;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteRunner implements CommandLineRunner {

    private final QuoteGeneratorService generatorService;
    private final QuoteMessageSender messageSender;
    private final Receiver receiver;

    @Override
    public void run(String... args) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(25);

        generatorService.fetchQuoteStream(Duration.ofMillis(100L))
                .take(25)
                .log("Got quote")
                .flatMap(messageSender::sendQuoteMessage)
                .subscribe(result -> {
                    log.debug("Sent message to Rabbit");

                    countDownLatch.countDown();
                    },
                        throwable -> log.error("Got error ", throwable),
                        () -> log.debug("All done"));

        countDownLatch.await(5, TimeUnit.SECONDS);

        AtomicInteger receivedCount = new AtomicInteger();

        receiver.consumeAutoAck(RabbitConfig.QUEUE)
                .log("Message Receiver")
                .subscribe(msg -> log.debug("Received message # {} - {}",
                receivedCount.incrementAndGet(), new String(msg.getBody())),
                        throwable -> log.error("Error receiving ", throwable),
                        () -> log.debug("All done from receiver"));
    }

}
