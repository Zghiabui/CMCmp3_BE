package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.dto.TopPlaylistDTO;
import com.example.CMCmp3.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getAll(Sort sort) {
        return ResponseEntity.ok(playlistService.getAllPlaylists(sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    @GetMapping("/top")
    public ResponseEntity<List<TopPlaylistDTO>> getTop(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopPlaylists(limit));
    }

    @GetMapping("/top/new-releases")
    public ResponseEntity<List<TopPlaylistDTO>> getTopNewReleases(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopPlaylistsByReleaseDate(limit));
    }

    @GetMapping("/top/most-liked")
    public ResponseEntity<List<TopPlaylistDTO>> getTopMostLiked(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(playlistService.getTopPlaylistsByLikes(limit));
    }
}
