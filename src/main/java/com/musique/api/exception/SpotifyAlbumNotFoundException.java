package com.musique.api.exception;

public class SpotifyAlbumNotFoundException extends RuntimeException {
    public SpotifyAlbumNotFoundException(String message) {
        super(message);
    }
}
