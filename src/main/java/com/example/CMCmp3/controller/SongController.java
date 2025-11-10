package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.dto.TopSongDTO;
import com.example.CMCmp3.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/songs")
@Tag(name = "song-controller")
public class SongController {

    private final SongService songService;

    // GET /api/songs
    @GetMapping
    public ResponseEntity<List<SongDTO>> getAll() {
        return ResponseEntity.ok(songService.getAll());
    }

    // GET /api/songs/top?limit=10
    @GetMapping("/top")
    public ResponseEntity<List<TopSongDTO>> getTop(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopSongs(limit));
    }

    // GET /api/songs/top/new-releases?limit=10
    @GetMapping("/top/new-releases")
    public ResponseEntity<List<TopSongDTO>> getTopNewReleases(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopSongsByReleaseDate(limit));
    }

    // GET /api/songs/top/most-liked?limit=10
    @GetMapping("/top/most-liked")
    public ResponseEntity<List<TopSongDTO>> getTopMostLiked(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopSongsByLikes(limit));
    }

    // GET /api/songs/{id}  (id là String theo Entity)
    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(songService.getById(id));
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> streamSong(@PathVariable String id) {
        Resource resource = songService.getSongFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
