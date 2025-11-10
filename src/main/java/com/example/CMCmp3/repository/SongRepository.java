package com.example.CMCmp3.repository;

import com.example.CMCmp3.dto.TopSongDTO;
import com.example.CMCmp3.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, String> {

    @Query("""
        SELECT new com.example.CMCmp3.dto.TopSongDTO(
            s.id,
            s.title,
            s.artist,
            s.imageUrl,
            s.listenCount
        )
        FROM Song s
        ORDER BY s.listenCount DESC
    """)
    List<TopSongDTO> findTopByListenCount(Pageable pageable);
}