package com.musique.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musique.api.dto.album.AlbumRequest;
import com.musique.api.dto.album.AlbumResponse;
import com.musique.api.dto.artist.ArtistSummaryResponse;
import com.musique.api.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlbumService albumService;

    @Test
    void shouldCreateAlbum() throws Exception {
        AlbumRequest request = new AlbumRequest(
                "Discovery",
                2001,
                4.5,
                "https://img.test/discovery.jpg",
                "ext-10",
                1L
        );
        AlbumResponse response = new AlbumResponse(
                10L,
                "Discovery",
                2001,
                4.5,
                "https://img.test/discovery.jpg",
                "ext-10",
                "https://open.spotify.com/album/ext-10",
                new ArtistSummaryResponse(1L, "Daft Punk")
        );
        when(albumService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.artist.id").value(1))
                .andExpect(jsonPath("$.artist.name").value("Daft Punk"));
    }

    @Test
    void shouldValidateAlbumRating() throws Exception {
        AlbumRequest request = new AlbumRequest("Discovery", 2001, 5.5, null, null, 1L);

        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateAlbumRatingStep() throws Exception {
        AlbumRequest request = new AlbumRequest("Discovery", 2001, 4.7, null, null, 1L);

        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindAlbumById() throws Exception {
        when(albumService.findById(10L)).thenReturn(new AlbumResponse(
                10L,
                "Discovery",
                2001,
                4.5,
                "https://img.test/discovery.jpg",
                "ext-10",
                "https://open.spotify.com/album/ext-10",
                new ArtistSummaryResponse(1L, "Daft Punk")
        ));

        mockMvc.perform(get("/api/albums/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Discovery"))
                .andExpect(jsonPath("$.artist.id").value(1))
                .andExpect(jsonPath("$.artist.name").value("Daft Punk"));
    }
}
