package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.ArtistDTO;
import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.service.ArtistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/artists")
@Tag(name = "artist-controller")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }
    @GetMapping("/{id}/songs")
    public ResponseEntity<List<SongDTO>> getSongsByArtist(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getSongsByArtistId(id));
    }
}
