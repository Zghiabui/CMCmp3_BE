package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    List<Playlist> findByUserId(Long userId);
    List<Playlist> findAllByNameContainingIgnoreCase(String query);
}
