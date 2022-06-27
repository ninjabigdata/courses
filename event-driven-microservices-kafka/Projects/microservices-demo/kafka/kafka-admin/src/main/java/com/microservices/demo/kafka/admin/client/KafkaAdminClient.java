package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAdminClient {

    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::doCreateTopic);
        } catch (Throwable throwable) {
            log.error("Error while creating topics", throwable);

            throw new KafkaClientException("Reached max number of retry for creating Kafka Topic",
                    throwable);
        }

        checkTopicsCreated();
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();

        int retryCount = 1;

        Integer maxRetry = retryConfigData.getMaxAttempts();
        Integer multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while ((!isTopicCreated(topics, topic))) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() {
        int retryCount = 1;

        Integer maxRetry = retryConfigData.getMaxAttempts();
        Integer multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient.get()
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchange()
                    .map(ClientResponse::statusCode)
                    .block();
        } catch (Exception exception) {
            log.error("Unable to connect to SchemaRegistry", exception);

            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics == null) {
            return false;
        }

        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private void checkMaxRetry(int retry, Integer maxRetry) {
        if (retry > maxRetry) {
            throw new KafkaClientException("Reached max number of retry for reading Kafka Topic(s)");
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for waiting new created topics", e);
        }
    }

    private CreateTopicsResult doCreateTopic(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();

        log.info("Creating {} topic(s), attempt {}", topicNames.size(), retryContext.getRetryCount());

        List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumOfPartitions(),
                kafkaConfigData.getReplicationFactor()
        )).collect(Collectors.toList());

        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;

        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Throwable throwable) {
            log.error("Error while creating topics", throwable);

            throw new KafkaClientException("Reached max number of retry for reading Kafka Topic", throwable);
        }

        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        log.info("Reading Kafka topic {}, attempt {}",
                kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());

        Collection<TopicListing> topics = adminClient.listTopics().listings().get();

        if (Objects.nonNull(topics)) {
            topics.forEach(topic -> log.info("Topic with name {}", topic.name()));
        }

        return topics;
    }

}
