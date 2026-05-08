package com.musique.api.service;

import com.musique.api.domain.entity.Album;
import com.musique.api.domain.entity.Artist;
import com.musique.api.domain.repository.AlbumRepository;
import com.musique.api.dto.album.AlbumRequest;
import com.musique.api.dto.album.AlbumResponse;
import com.musique.api.dto.artist.ArtistSummaryResponse;
import com.musique.api.integration.spotify.SpotifyAlbumData;
import com.musique.api.integration.spotify.SpotifyClient;
import com.musique.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    private final SpotifyClient spotifyClient;

    public AlbumService(AlbumRepository albumRepository, ArtistService artistService, SpotifyClient spotifyClient) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.spotifyClient = spotifyClient;
    }

    @Transactional
    public AlbumResponse create(AlbumRequest request) {
        Artist artist = artistService.findEntityById(request.artistId());
        Album album = new Album();
        applyRequest(album, request, artist);
        return toResponse(albumRepository.save(album));
    }

    @Transactional(readOnly = true)
    public List<AlbumResponse> findAll() {
        return albumRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlbumResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public AlbumResponse update(Long id, AlbumRequest request) {
        Artist artist = artistService.findEntityById(request.artistId());
        Album album = findEntityById(id);
        applyRequest(album, request, artist);
        return toResponse(albumRepository.save(album));
    }

    @Transactional
    public void delete(Long id) {
        Album album = findEntityById(id);
        albumRepository.delete(album);
    }

    Album findEntityById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("album com id " + id + " nao encontrado"));
    }

    private void applyRequest(Album album, AlbumRequest request, Artist artist) {
        SpotifyAlbumData spotifyData = spotifyClient.findAlbum(request.name(), artist.getName());
        album.setName(request.name());
        album.setReleaseYear(request.releaseYear());
        album.setRating(request.rating());
        album.setCoverUrl(spotifyData.coverUrl());
        album.setExternalId(spotifyData.spotifyId());
        album.setSpotifyUrl(spotifyData.spotifyUrl());
        album.setArtist(artist);
    }

    private AlbumResponse toResponse(Album album) {
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
