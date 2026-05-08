package com.musique.api.controller;

import com.musique.api.dto.artist.ArtistRequest;
import com.musique.api.dto.artist.ArtistResponse;
import com.musique.api.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistResponse create(@Valid @RequestBody ArtistRequest request) {
        return artistService.create(request);
    }

    @GetMapping
    public List<ArtistResponse> findAll() {
        return artistService.findAll();
    }

    @GetMapping("/{id}")
    public ArtistResponse findById(@PathVariable Long id) {
        return artistService.findById(id);
    }

    @PutMapping("/{id}")
    public ArtistResponse update(@PathVariable Long id, @Valid @RequestBody ArtistRequest request) {
        return artistService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        artistService.delete(id);
    }
}
