// src/main/java/com/example/CMCmp3/controller/PlaylistPublicController.java
package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.TopPlaylistDTO;
import com.example.CMCmp3.service.PlaylistQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/playlists")
public class PlaylistPublicController {

    private final PlaylistQueryService playlistQueryService;

    // GET /api/playlists/top?limit=8
    @GetMapping("/top")
    public List<TopPlaylistDTO> getTop(@RequestParam(defaultValue = "8") int limit) {
        return playlistQueryService.getTopPlaylists(limit);
    }
}
