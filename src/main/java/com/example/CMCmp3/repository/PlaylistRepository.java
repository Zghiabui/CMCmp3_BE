package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Playlist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    // === Overridden methods with JOIN FETCH to load related entities eagerly ===

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.playlistSongs ps LEFT JOIN FETCH ps.song")
    List<Playlist> findAll();

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.playlistSongs ps LEFT JOIN FETCH ps.song WHERE p.id = :id")
    Optional<Playlist> findById(@Param("id") Long id);

    // === Original methods preserved ===

    // Tìm playlist của một user cụ thể (Dùng ownerId)
    List<Playlist> findByOwnerId(Long ownerId);

    // Tìm kiếm theo tiêu đề (Title)
    @Query("SELECT p FROM Playlist p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Playlist> searchByTitle(@Param("query") String query);

    // --- TOP CHARTS (Modified with JOIN FETCH) ---

    // Top nghe nhiều
    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.playlistSongs ps LEFT JOIN FETCH ps.song ORDER BY p.playCount DESC")
    List<Playlist> findTopByPlayCount(Pageable pageable);

    // Top mới tạo
    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.playlistSongs ps LEFT JOIN FETCH ps.song ORDER BY p.createdAt DESC")
    List<Playlist> findTopByCreatedAt(Pageable pageable);

    // Top yêu thích
    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.playlistSongs ps LEFT JOIN FETCH ps.song ORDER BY p.likeCount DESC")
    List<Playlist> findTopByLikeCount(Pageable pageable);
}
