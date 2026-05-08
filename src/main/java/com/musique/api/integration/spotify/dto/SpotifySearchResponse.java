package com.musique.api.integration.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record SpotifySearchResponse(
        Albums albums
) {
    public record Albums(
            List<Item> items
    ) {
    }

    public record Item(
            String id,
            String name,
            List<Artist> artists,
            List<Image> images,
            @JsonProperty("external_urls")
            Map<String, String> externalUrls
    ) {
    }

    public record Artist(
            String name
    ) {
    }

    public record Image(
            String url
    ) {
    }
}
