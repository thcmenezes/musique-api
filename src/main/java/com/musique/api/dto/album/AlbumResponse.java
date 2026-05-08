package com.musique.api.dto.album;

import com.musique.api.dto.artist.ArtistSummaryResponse;

public record AlbumResponse(
        Long id,
        String name,
        Integer releaseYear,
        Double rating,
        String coverUrl,
        String idExternal,
        String spotifyUrl,
        ArtistSummaryResponse artist
) {
}
