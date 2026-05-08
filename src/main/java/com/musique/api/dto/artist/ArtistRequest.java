package com.musique.api.dto.artist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistRequest(
        @NotBlank(message = "nome do artista e obrigatorio")
        @Size(max = 120, message = "nome do artista deve ter no maximo 120 caracteres")
        String name
) {
}
