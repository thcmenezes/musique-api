package com.musique.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musique.api.dto.artist.ArtistRequest;
import com.musique.api.dto.artist.ArtistResponse;
import com.musique.api.service.ArtistService;
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

@WebMvcTest(ArtistController.class)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArtistService artistService;

    @Test
    void shouldCreateArtist() throws Exception {
        ArtistRequest request = new ArtistRequest("Daft Punk");
        ArtistResponse response = new ArtistResponse(1L, "Daft Punk");
        when(artistService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Daft Punk"));
    }

    @Test
    void shouldValidateArtistName() throws Exception {
        ArtistRequest request = new ArtistRequest("");

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindArtistById() throws Exception {
        when(artistService.findById(1L)).thenReturn(new ArtistResponse(1L, "Daft Punk"));

        mockMvc.perform(get("/api/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Daft Punk"));
    }
}
