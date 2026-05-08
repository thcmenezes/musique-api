ALTER TABLE albums
    ADD CONSTRAINT ck_albums_rating_range
        CHECK (rating >= 0.0 AND rating <= 5.0);

ALTER TABLE albums
    ADD CONSTRAINT ck_albums_rating_half_step
        CHECK (ABS((rating * 2) - ROUND(rating * 2)) < 0.000001);
