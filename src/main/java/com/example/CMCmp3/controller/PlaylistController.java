package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.CreatePlaylistDTO;
import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.dto.SongsToPlaylistDTO;
import com.example.CMCmp3.dto.UpdatePlaylistDTO;
import com.example.CMCmp3.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    // Lấy tất cả
    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getAll() {
        return ResponseEntity.ok(playlistService.getAll());
    }

    // Lấy chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.getById(id));
    }

    // API Top Nghe nhiều
    @GetMapping("/top")
    public ResponseEntity<List<PlaylistDTO>> getTop(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopPlaylistsByPlayCount(limit));
    }

    // API Top Mới nhất
    @GetMapping("/top/new")
    public ResponseEntity<List<PlaylistDTO>> getTopNew(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopNewPlaylists(limit));
    }

    // API Top Lượt thích
    @GetMapping("/top/likes")
    public ResponseEntity<List<PlaylistDTO>> getTopLikes(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopPlaylistsByLikeCount(limit));
    }

    // Tạo mới
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlaylistDTO> create(@Valid @RequestBody CreatePlaylistDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.createPlaylist(dto));
    }

    // Chỉnh sửa thông tin playlist
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlaylistDTO> update(@PathVariable Long id, @RequestBody UpdatePlaylistDTO dto) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, dto));
    }

    // Thêm một hoặc nhiều bài hát vào playlist
    @PostMapping("/{playlistId}/songs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlaylistDTO> addSongsToPlaylist(@PathVariable Long playlistId, @Valid @RequestBody SongsToPlaylistDTO dto) {
        return ResponseEntity.ok(playlistService.addSongsToPlaylist(playlistId, dto));
    }

    // Xóa bài hát khỏi playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.noContent().build();
    }

    // Xóa playlist
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }
}