package com.musique.api.controller;

import com.musique.api.dto.album.AlbumRequest;
import com.musique.api.dto.album.AlbumResponse;
import com.musique.api.service.AlbumService;
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
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponse create(@Valid @RequestBody AlbumRequest request) {
        return albumService.create(request);
    }

    @GetMapping
    public List<AlbumResponse> findAll() {
        return albumService.findAll();
    }

    @GetMapping("/{id}")
    public AlbumResponse findById(@PathVariable Long id) {
        return albumService.findById(id);
    }

    @PutMapping("/{id}")
    public AlbumResponse update(@PathVariable Long id, @Valid @RequestBody AlbumRequest request) {
        return albumService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        albumService.delete(id);
    }
}
