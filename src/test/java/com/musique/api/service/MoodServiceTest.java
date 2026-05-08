package com.musique.api.service;

import com.musique.api.domain.entity.Album;
import com.musique.api.domain.entity.Artist;
import com.musique.api.domain.entity.MoodAlbum;
import com.musique.api.domain.repository.MoodAlbumRepository;
import com.musique.api.dto.mood.MoodAlbumRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoodServiceTest {

    @Mock
    private MoodAlbumRepository moodAlbumRepository;

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private MoodService moodService;

    @Test
    void shouldAddAlbumToMood() {
        when(moodAlbumRepository.existsByAlbumId(10L)).thenReturn(false);
        Album album = buildAlbum();
        when(albumService.findEntityById(10L)).thenReturn(album);

        moodService.add(new MoodAlbumRequest(10L));

        verify(moodAlbumRepository).save(any(MoodAlbum.class));
    }

    @Test
    void shouldNotAddDuplicatedAlbumToMood() {
        when(moodAlbumRepository.existsByAlbumId(10L)).thenReturn(true);

        moodService.add(new MoodAlbumRequest(10L));

        verify(moodAlbumRepository, never()).save(any(MoodAlbum.class));
    }

    @Test
    void shouldListMoodAlbums() {
        MoodAlbum moodAlbum = new MoodAlbum();
        moodAlbum.setId(1L);
        moodAlbum.setAlbum(buildAlbum());
        when(moodAlbumRepository.findAllByOrderByIdDesc()).thenReturn(List.of(moodAlbum));

        var response = moodService.findAll();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().name()).isEqualTo("Discovery");
        assertThat(response.getFirst().artist().name()).isEqualTo("Daft Punk");
    }

    private Album buildAlbum() {
        Artist artist = new Artist();
        artist.setId(1L);
        artist.setName("Daft Punk");

        Album album = new Album();
        album.setId(10L);
        album.setName("Discovery");
        album.setReleaseYear(2001);
        album.setRating(4.5);
        album.setArtist(artist);
        return album;
    }
}
