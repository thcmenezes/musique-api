package com.musique.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musique.api.dto.album.AlbumResponse;
import com.musique.api.dto.artist.ArtistSummaryResponse;
import com.musique.api.dto.mood.MoodAlbumRequest;
import com.musique.api.service.MoodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MoodController.class)
class MoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MoodService moodService;

    @Test
    void shouldListMoodAlbums() throws Exception {
        when(moodService.findAll()).thenReturn(List.of(
                new AlbumResponse(
                        10L,
                        "Discovery",
                        2001,
                        4.5,
                        "https://img.test/discovery.jpg",
                        "ext-10",
                        "https://open.spotify.com/album/ext-10",
                        new ArtistSummaryResponse(1L, "Daft Punk")
                )
        ));

        mockMvc.perform(get("/api/mood"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].artist.name").value("Daft Punk"));
    }

    @Test
    void shouldAddAlbumToMood() throws Exception {
        MoodAlbumRequest request = new MoodAlbumRequest(10L);

        mockMvc.perform(post("/api/mood")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRemoveAlbumFromMood() throws Exception {
        mockMvc.perform(delete("/api/mood/10"))
                .andExpect(status().isNoContent());
    }
}
