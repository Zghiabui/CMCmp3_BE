package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findAllByNameContainingIgnoreCase(String name);
    Optional<Artist> findByName(String name);
    boolean existsByName(String name);
    @Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Artist> searchByName(@Param("query") String query);
}
