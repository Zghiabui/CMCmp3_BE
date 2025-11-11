package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.Playlist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    // Tìm playlist của một user cụ thể (Dùng ownerId)
    List<Playlist> findByOwnerId(Long ownerId);



    // Tìm kiếm theo tiêu đề (Title)
    @Query("SELECT p FROM Playlist p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Playlist> searchByTitle(@Param("query") String query);

    // --- TOP CHARTS (Trả về Entity để Service map sang DTO) ---

    // Top nghe nhiều
    @Query("SELECT p FROM Playlist p ORDER BY p.playCount DESC")
    List<Playlist> findTopByPlayCount(Pageable pageable);

    // Top mới tạo
    @Query("SELECT p FROM Playlist p ORDER BY p.createdAt DESC")
    List<Playlist> findTopByCreatedAt(Pageable pageable);

    // Top yêu thích
    @Query("SELECT p FROM Playlist p ORDER BY p.likeCount DESC")
    List<Playlist> findTopByLikeCount(Pageable pageable);
}