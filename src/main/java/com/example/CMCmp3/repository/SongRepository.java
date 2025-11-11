package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN s.artists a WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Song> searchSongsByTitleOrArtist(@Param("query") String query);


    List<Song> findAllByArtistsId(Long artistId);


    @Query("SELECT s FROM Song s ORDER BY s.listenCount DESC")
    List<Song> findTopByListenCount(Pageable pageable);


    @Query("SELECT s FROM Song s ORDER BY s.createdAt DESC")
    List<Song> findTopByCreatedAt(Pageable pageable);


    @Query("SELECT s FROM Song s ORDER BY s.likeCount DESC")
    List<Song> findTopByLikeCount(Pageable pageable);
}
