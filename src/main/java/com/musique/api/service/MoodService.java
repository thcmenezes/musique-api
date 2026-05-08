package com.musique.api.service;

import com.musique.api.domain.entity.Album;
import com.musique.api.domain.entity.MoodAlbum;
import com.musique.api.domain.repository.MoodAlbumRepository;
import com.musique.api.dto.album.AlbumResponse;
import com.musique.api.dto.artist.ArtistSummaryResponse;
import com.musique.api.dto.mood.MoodAlbumRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MoodService {

    private final MoodAlbumRepository moodAlbumRepository;
    private final AlbumService albumService;

    public MoodService(MoodAlbumRepository moodAlbumRepository, AlbumService albumService) {
        this.moodAlbumRepository = moodAlbumRepository;
        this.albumService = albumService;
    }

    @Transactional(readOnly = true)
    public List<AlbumResponse> findAll() {
        return moodAlbumRepository.findAllByOrderByIdDesc()
                .stream()
                .map(MoodAlbum::getAlbum)
                .map(this::toAlbumResponse)
                .toList();
    }

    @Transactional
    public void add(MoodAlbumRequest request) {
        if (moodAlbumRepository.existsByAlbumId(request.albumId())) {
            return;
        }
        Album album = albumService.findEntityById(request.albumId());
        MoodAlbum moodAlbum = new MoodAlbum();
        moodAlbum.setAlbum(album);
        moodAlbumRepository.save(moodAlbum);
    }

    @Transactional
    public void remove(Long albumId) {
        moodAlbumRepository.deleteByAlbumId(albumId);
    }

    private AlbumResponse toAlbumResponse(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getName(),
                album.getReleaseYear(),
                album.getRating(),
                album.getCoverUrl(),
                album.getExternalId(),
                album.getSpotifyUrl(),
                new ArtistSummaryResponse(
                        album.getArtist().getId(),
                        album.getArtist().getName()
                )
        );
    }
}
