package com.musique.api.service;

import com.musique.api.domain.entity.Artist;
import com.musique.api.domain.repository.ArtistRepository;
import com.musique.api.dto.artist.ArtistRequest;
import com.musique.api.dto.artist.ArtistResponse;
import com.musique.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional
    public ArtistResponse create(ArtistRequest request) {
        Artist artist = new Artist();
        artist.setName(request.name());
        return toResponse(artistRepository.save(artist));
    }

    @Transactional(readOnly = true)
    public List<ArtistResponse> findAll() {
        return artistRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArtistResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public ArtistResponse update(Long id, ArtistRequest request) {
        Artist artist = findEntityById(id);
        artist.setName(request.name());
        return toResponse(artistRepository.save(artist));
    }

    @Transactional
    public void delete(Long id) {
        Artist artist = findEntityById(id);
        artistRepository.delete(artist);
    }

    public Artist findEntityById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("artista com id " + id + " nao encontrado"));
    }

    private ArtistResponse toResponse(Artist artist) {
        return new ArtistResponse(artist.getId(), artist.getName());
    }
}
