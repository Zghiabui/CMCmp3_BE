package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.PlaylistSong;
import com.example.CMCmp3.entity.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    Optional<PlaylistSong> findByPlaylistIdAndSongId(Long playlistId, Long songId);
}
