package com.musique.api.dto.album;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.AssertTrue;

public record AlbumRequest(
        @NotBlank(message = "nome do album e obrigatorio")
        @Size(max = 160, message = "nome do album deve ter no maximo 160 caracteres")
        String name,

        @NotNull(message = "ano de lancamento e obrigatorio")
        @Min(value = 1800, message = "ano de lancamento invalido")
        @Max(value = 3000, message = "ano de lancamento invalido")
        Integer releaseYear,

        @NotNull(message = "rating e obrigatorio")
        @DecimalMin(value = "0.0", message = "rating deve ser entre 0 e 5")
        @DecimalMax(value = "5.0", message = "rating deve ser entre 0 e 5")
        Double rating,

        @Size(max = 500, message = "coverUrl deve ter no maximo 500 caracteres")
        String coverUrl,

        @Size(max = 100, message = "idExternal deve ter no maximo 100 caracteres")
        String idExternal,

        @NotNull(message = "artistaId e obrigatorio")
        @Positive(message = "artistaId deve ser positivo")
        Long artistId
) {
    @AssertTrue(message = "rating deve usar incrementos de 0.5")
    public boolean isRatingStepValid() {
        if (rating == null) {
            return true;
        }
        double scaled = rating * 2;
        return Math.abs(scaled - Math.rint(scaled)) < 1e-9;
    }
}
