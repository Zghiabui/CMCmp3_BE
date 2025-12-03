package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreateArtistRequestDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.ArtistRepository;
import com.example.CMCmp3.repository.ArtistVerificationRequestRepository;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistVerificationService {

    private final ArtistVerificationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    @Transactional
    public ArtistVerificationRequest createRequest(CreateArtistRequestDTO dto, User user) {
        // Optional: Check if user already has a pending request or is already an artist
        if (user.getRole() == Role.ARTIST || requestRepository.existsByUserAndStatus(user, RequestStatus.PENDING)) {
            throw new IllegalStateException("User is already an artist or has a pending verification request.");
        }

        ArtistVerificationRequest request = new ArtistVerificationRequest();
        request.setUser(user);
        request.setArtistName(dto.getArtistName());
        request.setImageUrl(dto.getImageUrl());
        // Status and requestDate are set by @PrePersist
        return requestRepository.save(request);
    }

    public List<ArtistVerificationRequest> getPendingRequests() {
        return requestRepository.findByStatus(RequestStatus.PENDING);
    }
    
    @Transactional
    public void approveRequest(Long requestId) {
        ArtistVerificationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not in PENDING state.");
        }

        User user = request.getUser();

        // Create a new Artist
        Artist artist = new Artist();
        artist.setName(request.getArtistName());
        artist.setAvatarUrl(request.getImageUrl());
        artist.setCreatedAt(LocalDateTime.now());
        Artist savedArtist = artistRepository.save(artist);

        // Update User
        user.setRole(Role.ARTIST);
        user.setArtist(savedArtist);
        userRepository.save(user);

        // Update Request status
        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(Long requestId) {
        ArtistVerificationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not in PENDING state.");
        }

        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);
    }
}
