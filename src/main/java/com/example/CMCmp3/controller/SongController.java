package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    // GET /api/songs/{id}  (id là String theo Entity)
    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(songService.getById(id));
    }
}
