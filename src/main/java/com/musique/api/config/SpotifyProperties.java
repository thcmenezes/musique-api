package com.musique.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.spotify")
public record SpotifyProperties(
        String clientId,
        String clientSecret,
        String accountsBaseUrl,
        String apiBaseUrl
) {
}
