CREATE TABLE mood_albums (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL,
    CONSTRAINT fk_mood_albums_album
        FOREIGN KEY (album_id)
        REFERENCES albums (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_mood_albums_album UNIQUE (album_id)
);
