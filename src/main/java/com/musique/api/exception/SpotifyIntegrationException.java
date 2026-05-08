package com.musique.api.exception;

public class SpotifyIntegrationException extends RuntimeException {
    public SpotifyIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
