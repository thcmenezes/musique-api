package com.musique.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(SpotifyProperties.class)
public class SpotifyConfig {

    @Bean
    public WebClient spotifyApiClient(WebClient.Builder builder) {
        return builder.build();
    }
}
