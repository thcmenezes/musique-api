package com.musique.api.domain.repository;

import com.musique.api.domain.entity.MoodAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoodAlbumRepository extends JpaRepository<MoodAlbum, Long> {
    boolean existsByAlbumId(Long albumId);

    void deleteByAlbumId(Long albumId);

    List<MoodAlbum> findAllByOrderByIdDesc();
}
