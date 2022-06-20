package com.microservices.demo.twitter.to.kafka.service.initializer;

import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class TwitterStreamInitializer implements CommandLineRunner {

    private final TwitterToKafkaServiceConfigData configData;

    @Override
    public void run(String... args) {
        log.info("App starts");

        log.info(Arrays.toString(configData.getTwitterKeywords().toArray(new String[] {})));
        log.info(configData.getWelcomeMessage());
    }
}
