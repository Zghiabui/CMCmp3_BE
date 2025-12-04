package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.ArtistRequestDTO;
import com.example.CMCmp3.entity.ArtistVerificationRequest;
import com.example.CMCmp3.service.ArtistVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/artist-verification-requests")
@RequiredArgsConstructor
public class AdminArtistVerificationController {

    private final ArtistVerificationService artistVerificationService;

    @GetMapping
    public ResponseEntity<List<ArtistRequestDTO>> getPendingRequests() {
        List<ArtistVerificationRequest> requests = artistVerificationService.getPendingRequests();
        List<ArtistRequestDTO> dtos = requests.stream()
                .map(ArtistRequestDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        try {
            artistVerificationService.approveRequest(id);
            return ResponseEntity.ok().body("Request approved successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        try {
            artistVerificationService.rejectRequest(id);
            return ResponseEntity.ok().body("Request rejected successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
