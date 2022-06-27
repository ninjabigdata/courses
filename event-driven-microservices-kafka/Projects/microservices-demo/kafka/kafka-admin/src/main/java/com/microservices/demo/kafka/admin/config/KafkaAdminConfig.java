package com.microservices.demo.kafka.admin.config;

import com.microservices.demo.config.KafkaConfigData;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;

@RequiredArgsConstructor
@EnableRetry
@Configuration
public class KafkaAdminConfig {

    private final KafkaConfigData configData;

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(Map.of(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configData.getBootstrapServers()
        ));
    }

}
