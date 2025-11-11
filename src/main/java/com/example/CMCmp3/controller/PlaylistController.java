package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.CreatePlaylistDTO;
import com.example.CMCmp3.dto.PlaylistDTO;
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

    private final PlaylistService playlistService; // Chỉ cần inject Service chính

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

    // API Top Nghe nhiều (Thay thế cho QueryService cũ)
    @GetMapping("/top")
    public ResponseEntity<List<PlaylistDTO>> getTop(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopPlaylistsByPlayCount(limit));
    }

    // API Top Mới nhất
    @GetMapping("/top/new")
    public ResponseEntity<List<PlaylistDTO>> getTopNew(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopNewPlaylists(limit));
    }

    // Tạo mới
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlaylistDTO> create(@Valid @RequestBody CreatePlaylistDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.createPlaylist(dto));
    }

    // Xóa
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }
}