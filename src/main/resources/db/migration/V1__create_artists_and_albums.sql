CREATE TABLE artists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE albums (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    release_year INTEGER NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    cover_url VARCHAR(500),
    id_external VARCHAR(100),
    artist_id BIGINT NOT NULL,
    CONSTRAINT fk_albums_artist
        FOREIGN KEY (artist_id)
        REFERENCES artists (id)
        ON DELETE RESTRICT
);
