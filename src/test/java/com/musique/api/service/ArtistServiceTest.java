package com.musique.api.service;

import com.musique.api.domain.entity.Artist;
import com.musique.api.domain.repository.ArtistRepository;
import com.musique.api.dto.artist.ArtistRequest;
import com.musique.api.dto.artist.ArtistResponse;
import com.musique.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void shouldCreateArtist() {
        ArtistRequest request = new ArtistRequest("Daft Punk");
        Artist saved = new Artist();
        saved.setId(1L);
        saved.setName("Daft Punk");

        when(artistRepository.save(org.mockito.ArgumentMatchers.any(Artist.class))).thenReturn(saved);

        ArtistResponse response = artistService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Daft Punk");
    }

    @Test
    void shouldThrowWhenArtistNotFound() {
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("artista com id 99");
    }
}
