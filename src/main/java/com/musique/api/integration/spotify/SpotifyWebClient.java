package com.musique.api.integration.spotify;

import com.musique.api.config.SpotifyProperties;
import com.musique.api.exception.SpotifyAlbumAmbiguousException;
import com.musique.api.exception.SpotifyAlbumNotFoundException;
import com.musique.api.exception.SpotifyIntegrationException;
import com.musique.api.integration.spotify.dto.SpotifySearchResponse;
import com.musique.api.integration.spotify.dto.SpotifyTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Component
public class SpotifyWebClient implements SpotifyClient {

    private static final int SEARCH_LIMIT = 8;
    private static final long TOKEN_REFRESH_BUFFER_SECONDS = 30;

    private final WebClient webClient;
    private final SpotifyProperties spotifyProperties;

    private volatile String accessToken;
    private volatile Instant tokenExpiresAt = Instant.EPOCH;
    private final Object tokenLock = new Object();

    public SpotifyWebClient(WebClient spotifyWebClient, SpotifyProperties spotifyProperties) {
        this.webClient = spotifyWebClient;
        this.spotifyProperties = spotifyProperties;
    }

    @Override
    public SpotifyAlbumData findAlbum(String albumName, String artistName) {
        validateSpotifyConfiguration();
        String token = getOrRefreshAccessToken();
        String query = "album:" + albumName + " artist:" + artistName;
        try {
            SpotifySearchResponse response = webClient.get()
                    .uri(spotifyProperties.apiBaseUrl() + "/v1/search?q={q}&type=album&limit={limit}", query, SEARCH_LIMIT)
                    .headers(headers -> {
                        headers.setBearerAuth(token);
                        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                    })
                    .retrieve()
                    .bodyToMono(SpotifySearchResponse.class)
                    .block();

            List<SpotifySearchResponse.Item> items = Optional.ofNullable(response)
                    .map(SpotifySearchResponse::albums)
                    .map(SpotifySearchResponse.Albums::items)
                    .orElse(List.of());

            if (items.isEmpty()) {
                throw new SpotifyAlbumNotFoundException(
                        "Nao encontramos este album no Spotify. Verifique o nome do album e do artista."
                );
            }

            return selectBestMatch(items, albumName, artistName);
        } catch (SpotifyAlbumNotFoundException | SpotifyAlbumAmbiguousException e) {
            throw e;
        } catch (Exception e) {
            throw new SpotifyIntegrationException("Falha ao consultar dados do Spotify.", e);
        }
    }

    private SpotifyAlbumData selectBestMatch(List<SpotifySearchResponse.Item> items, String albumName, String artistName) {
        List<ScoredItem> ranked = items.stream()
                .map(item -> new ScoredItem(item, score(item, albumName, artistName)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredItem::score).reversed())
                .toList();

        if (ranked.isEmpty()) {
            throw new SpotifyAlbumNotFoundException(
                    "Nao encontramos correspondencia confiavel para este album no Spotify."
            );
        }

        if (ranked.size() > 1 && ranked.get(0).score() == ranked.get(1).score()) {
            throw new SpotifyAlbumAmbiguousException(
                    "Encontramos mais de um resultado possivel no Spotify. Ajuste o nome do album/artista e tente novamente."
            );
        }

        SpotifySearchResponse.Item selected = ranked.getFirst().item();
        String spotifyUrl = Optional.ofNullable(selected.externalUrls())
                .map(urls -> urls.get("spotify"))
                .orElse(null);
        String coverUrl = Optional.ofNullable(selected.images())
                .stream()
                .flatMap(List::stream)
                .map(SpotifySearchResponse.Image::url)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return new SpotifyAlbumData(selected.id(), spotifyUrl, coverUrl);
    }

    private int score(SpotifySearchResponse.Item item, String albumName, String artistName) {
        String normalizedAlbum = normalize(albumName);
        String normalizedArtist = normalize(artistName);
        String itemAlbum = normalize(item.name());
        List<String> itemArtists = Optional.ofNullable(item.artists())
                .orElse(List.of())
                .stream()
                .map(SpotifySearchResponse.Artist::name)
                .map(this::normalize)
                .toList();

        boolean exactAlbum = itemAlbum.equals(normalizedAlbum);
        boolean closeAlbum = itemAlbum.contains(normalizedAlbum) || normalizedAlbum.contains(itemAlbum);
        boolean exactArtist = itemArtists.stream().anyMatch(artist -> artist.equals(normalizedArtist));
        boolean closeArtist = itemArtists.stream().anyMatch(artist -> artist.contains(normalizedArtist));

        int score = 0;
        if (exactAlbum) score += 100;
        else if (closeAlbum) score += 40;

        if (exactArtist) score += 80;
        else if (closeArtist) score += 20;

        return score;
    }

    private String getOrRefreshAccessToken() {
        if (accessToken != null && Instant.now().isBefore(tokenExpiresAt.minusSeconds(TOKEN_REFRESH_BUFFER_SECONDS))) {
            return accessToken;
        }
        synchronized (tokenLock) {
            if (accessToken != null && Instant.now().isBefore(tokenExpiresAt.minusSeconds(TOKEN_REFRESH_BUFFER_SECONDS))) {
                return accessToken;
            }
            SpotifyTokenResponse tokenResponse = requestAccessToken();
            this.accessToken = tokenResponse.accessToken();
            this.tokenExpiresAt = Instant.now().plusSeconds(Optional.ofNullable(tokenResponse.expiresIn()).orElse(3600L));
            return this.accessToken;
        }
    }

    private SpotifyTokenResponse requestAccessToken() {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "client_credentials");

            return webClient.post()
                    .uri(spotifyProperties.accountsBaseUrl() + "/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodeClientCredentials())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(SpotifyTokenResponse.class)
                    .block();
        } catch (Exception e) {
            throw new SpotifyIntegrationException("Falha ao autenticar na API do Spotify.", e);
        }
    }

    private String encodeClientCredentials() {
        String credentials = spotifyProperties.clientId() + ":" + spotifyProperties.clientSecret();
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private void validateSpotifyConfiguration() {
        if (isBlank(spotifyProperties.clientId()) || isBlank(spotifyProperties.clientSecret())) {
            throw new SpotifyIntegrationException(
                    "Credenciais do Spotify nao configuradas. Defina APP_SPOTIFY_CLIENT_ID e APP_SPOTIFY_CLIENT_SECRET.",
                    null
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return Optional.ofNullable(value)
                .orElse("")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private record ScoredItem(SpotifySearchResponse.Item item, int score) {
    }
}
