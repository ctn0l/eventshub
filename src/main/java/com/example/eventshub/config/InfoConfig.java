package com.example.eventshub.config;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class InfoConfig {

    @Bean
    public InfoContributor appInfoContributor() {
        return builder -> builder.withDetail("app", Map.of(
                "name", "eventshub",
                "version", "0.0.1"
        ));
    }
}