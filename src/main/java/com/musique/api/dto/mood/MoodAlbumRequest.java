package com.musique.api.dto.mood;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MoodAlbumRequest(
        @NotNull(message = "albumId e obrigatorio")
        @Positive(message = "albumId deve ser positivo")
        Long albumId
) {
}
