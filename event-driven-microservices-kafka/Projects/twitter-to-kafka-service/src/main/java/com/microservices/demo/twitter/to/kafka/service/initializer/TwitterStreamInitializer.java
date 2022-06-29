package com.microservices.demo.twitter.to.kafka.service.initializer;

import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TwitterStreamInitializer implements CommandLineRunner {

    private final StreamRunner streamRunner;
    private final StreamInitializer streamInitializer;

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts");

        streamInitializer.init();
        streamRunner.start();
    }
}
