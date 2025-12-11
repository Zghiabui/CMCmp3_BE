package com.example.CMCmp3.repository;

import com.example.CMCmp3.entity.ArtistVerificationRequest;
import com.example.CMCmp3.entity.RequestStatus;
import com.example.CMCmp3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistVerificationRequestRepository extends JpaRepository<ArtistVerificationRequest, Long> {
    boolean existsByUserAndStatus(User user, RequestStatus status);
    List<ArtistVerificationRequest> findByStatus(RequestStatus status);
}
