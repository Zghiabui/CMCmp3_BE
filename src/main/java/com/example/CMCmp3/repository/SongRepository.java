package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Song;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;



import java.util.List;



@Repository

public interface SongRepository extends JpaRepository<Song, String> {

    @Query("SELECT s FROM Song s LEFT JOIN s.artist a WHERE " +

            "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +

            "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")

    List<Song> searchSongsByTitleOrArtist(@Param("query") String query);

    List<Song> findAllByArtistId(Long artistId);

}
