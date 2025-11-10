package com.example.CMCmp3.repository;

import com.example.CMCmp3.dto.TopSongDTO;
import com.example.CMCmp3.entity.Song;
<<<<<<< HEAD

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;



import java.util.List;



=======
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

>>>>>>> afbce9943ccf7b98f1f3880663cc6d93e11f06d5
@Repository

public interface SongRepository extends JpaRepository<Song, String> {

<<<<<<< HEAD
    @Query("SELECT s FROM Song s LEFT JOIN s.artist a WHERE " +

            "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +

            "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")

    List<Song> searchSongsByTitleOrArtist(@Param("query") String query);

    List<Song> findAllByArtistId(Long artistId);

}
=======
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

    @Query("""
        SELECT new com.example.CMCmp3.dto.TopSongDTO(
            s.id,
            s.title,
            s.artist,
            s.imageUrl,
            s.listenCount
        )
        FROM Song s
        ORDER BY s.createdAt DESC
    """)
    List<TopSongDTO> findTopByCreatedAt(Pageable pageable);

    @Query("""
        SELECT new com.example.CMCmp3.dto.TopSongDTO(
            s.id,
            s.title,
            s.artist,
            s.imageUrl,
            s.listenCount
        )
        FROM Song s
        ORDER BY s.likeCount DESC
    """)
    List<TopSongDTO> findTopByLikeCount(Pageable pageable);
}
>>>>>>> afbce9943ccf7b98f1f3880663cc6d93e11f06d5
