package com.example.CMCmp3.repository;

import com.example.CMCmp3.dto.TopPlaylistDTO;
import com.example.CMCmp3.entity.Playlist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    List<Playlist> findByUserId(Long userId);
    List<Playlist> findAllByNameContainingIgnoreCase(String query);
    // Nếu User có field displayName và username:
    @Query("""
        SELECT new com.example.CMCmp3.dto.TopPlaylistDTO(
            p.id,
            p.name,
            p.imageUrl,
            CAST(p.listenCount AS long),
            COALESCE(u.displayName, u.username)
        )
        FROM Playlist p
        LEFT JOIN p.user u
        ORDER BY p.listenCount DESC
    """)
    List<TopPlaylistDTO> findTopByListenCount(Pageable pageable);
}
