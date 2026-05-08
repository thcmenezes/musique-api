package com.musique.api.service;

import com.musique.api.domain.entity.Album;
import com.musique.api.domain.entity.Artist;
import com.musique.api.domain.repository.AlbumRepository;
import com.musique.api.dto.album.AlbumRequest;
import com.musique.api.dto.album.AlbumResponse;
import com.musique.api.integration.spotify.SpotifyAlbumData;
import com.musique.api.integration.spotify.SpotifyClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistService artistService;

    @Mock
    private SpotifyClient spotifyClient;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void shouldCreateAlbum() {
        AlbumRequest request = new AlbumRequest(
                "Discovery",
                2001,
                4.5,
                "https://img.test/discovery.jpg",
                "ext-123",
                1L
        );

        Artist artist = new Artist();
        artist.setId(1L);
        artist.setName("Daft Punk");

        Album saved = new Album();
        saved.setId(10L);
        saved.setName("Discovery");
        saved.setReleaseYear(2001);
        saved.setRating(4.5);
        saved.setCoverUrl("https://img.test/discovery.jpg");
        saved.setExternalId("ext-123");
        saved.setSpotifyUrl("https://open.spotify.com/album/ext-123");
        saved.setArtist(artist);

        when(artistService.findEntityById(1L)).thenReturn(artist);
        when(spotifyClient.findAlbum("Discovery", "Daft Punk"))
                .thenReturn(new SpotifyAlbumData(
                        "ext-123",
                        "https://open.spotify.com/album/ext-123",
                        "https://img.test/discovery.jpg"
                ));
        when(albumRepository.save(any(Album.class))).thenReturn(saved);

        AlbumResponse response = albumService.create(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.artist().id()).isEqualTo(1L);
        assertThat(response.artist().name()).isEqualTo("Daft Punk");
        assertThat(response.name()).isEqualTo("Discovery");
    }
}
