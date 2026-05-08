package com.musique.api.exception;

public class SpotifyAlbumAmbiguousException extends RuntimeException {
    public SpotifyAlbumAmbiguousException(String message) {
        super(message);
    }
}
