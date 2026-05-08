package com.musique.api.integration.spotify;

public interface SpotifyClient {
    SpotifyAlbumData findAlbum(String albumName, String artistName);
}
