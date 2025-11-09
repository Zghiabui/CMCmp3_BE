package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findAllByNameContainingIgnoreCase(String name);
}
