package com.musique.api.controller;

import com.musique.api.dto.album.AlbumResponse;
import com.musique.api.dto.mood.MoodAlbumRequest;
import com.musique.api.service.MoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mood")
public class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    @GetMapping
    public List<AlbumResponse> findAll() {
        return moodService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@Valid @RequestBody MoodAlbumRequest request) {
        moodService.add(request);
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long albumId) {
        moodService.remove(albumId);
    }
}
