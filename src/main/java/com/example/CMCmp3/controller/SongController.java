package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.CreateSongDTO;
import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.dto.AddLyricsDTO;
import com.example.CMCmp3.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;

import java.util.List;

import java.util.Map;

import java.util.Set;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping
    public ResponseEntity<Page<SongDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(songService.getAllSongs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getById(id));
    }


    @GetMapping("/{id}/download")


    @PreAuthorize("isAuthenticated()")


    public ResponseEntity<Resource> downloadSong(@PathVariable Long id) throws IOException {

        Map<String, Object> songData = songService.getSongResource(id);

        Resource resource = (Resource) songData.get("resource");

        String filename = (String) songData.get("filename");


        return ResponseEntity.ok()

                .contentType(MediaType.APPLICATION_OCTET_STREAM)

                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")

                .body(resource);

    }


    @GetMapping("/top")
    public ResponseEntity<List<SongDTO>> getTop(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopSongs(limit));
    }

    @GetMapping("/top/new-releases")
    public ResponseEntity<List<SongDTO>> getTopNew(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopNewReleases(limit));
    }

    @GetMapping("/top/most-liked")
    public ResponseEntity<List<SongDTO>> getTopLiked(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(songService.getTopMostLiked(limit));
    }

    @GetMapping("/uploaded")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SongDTO>> getUploadedSongs() {
        return ResponseEntity.ok(songService.getUploadedSongsForCurrentUser());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SongDTO>> getSongsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(songService.getSongsByUserId(userId));
    }

    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SongDTO>> getFavoriteSongs() {
        return ResponseEntity.ok(songService.getFavoriteSongsForCurrentUser());
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongDTO> create(@Valid @RequestBody CreateSongDTO dto) {
        return ResponseEntity.ok(songService.createSong(dto));
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SongDTO> uploadSong(
            @RequestParam("songFile") MultipartFile songFile,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "artistIds", required = false) Set<Long> artistIds,
            @RequestParam(value = "tagIds", required = false) Set<Long> tagIds) {

        SongDTO newSong = songService.createSongWithUpload(title, description, artistIds, tagIds, songFile, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSong);
    }

    @PostMapping("/{id}/lyrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongDTO> addLyrics(@PathVariable Long id, @RequestBody AddLyricsDTO dto) {
        return ResponseEntity.ok(songService.addLyricsToSong(id, dto));
    }

    @PutMapping(value = "/uploaded/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SongDTO> updateUploadedSong(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "artistIds", required = false) Set<Long> artistIds,
            @RequestParam(value = "tagIds", required = false) Set<Long> tagIds,
            @RequestParam(value = "songFile", required = false) MultipartFile songFile,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "status", required = false) String status
    ) {
        SongDTO updatedSong = songService.updateUploadedSong(id, title, description, artistIds, tagIds, songFile, imageFile, status);
        return ResponseEntity.ok(updatedSong);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongDTO> update(@PathVariable Long id, @RequestBody CreateSongDTO dto) {
        return ResponseEntity.ok(songService.updateSong(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> like(@PathVariable Long id) {
        songService.likeSong(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlike(@PathVariable Long id) {
        songService.unlikeSong(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/listen")


    public ResponseEntity<Void> incrementListenCount(@PathVariable Long id) {


        songService.incrementListenCount(id);


        return ResponseEntity.ok().build();


    }


    // TODO: Configure the base URL in a more flexible way


    @GetMapping("/{id}/share")


    public ResponseEntity<String> shareSong(@PathVariable Long id) {


        String shareUrl = "http://localhost:3000/songs/" + id;


        return ResponseEntity.ok(shareUrl);


    }
}

