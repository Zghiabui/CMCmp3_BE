package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreateSongCommentDTO;
import com.example.CMCmp3.dto.SongCommentDTO;
import com.example.CMCmp3.dto.UpdateSongCommentDTO;
import com.example.CMCmp3.entity.*;

import com.example.CMCmp3.repository.SongCommentRepository;
import com.example.CMCmp3.repository.SongRepository;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SongCommentService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SongCommentRepository songCommentRepository;
    private final NotificationService notificationService;


    private User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));
    }

    private SongCommentDTO toDTO(SongComment comment) {
        return new SongCommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                SongCommentDTO.fromUser(comment.getUser())
        );
    }

    @Transactional
    public SongCommentDTO addCommentToSong(Long songId, CreateSongCommentDTO commentDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new NoSuchElementException("Song not found with ID: " + songId));

        SongComment newComment = SongComment.builder()
                .song(song)
                .user(currentUser)
                .content(commentDTO.getContent())
                .build();

        SongComment savedComment = songCommentRepository.save(newComment);
        return toDTO(savedComment);
    }

    @Transactional(readOnly = true)
    public Page<SongCommentDTO> getCommentsBySongId(Long songId, Pageable pageable) {
        if (!songRepository.existsById(songId)) {
            throw new NoSuchElementException("Song not found with ID: " + songId);
        }
        return songCommentRepository.findBySongIdAndStatus(songId, CommentStatus.APPROVED, pageable).map(this::toDTO);
    }

    @Transactional
    public SongCommentDTO updateComment(Long commentId, UpdateSongCommentDTO commentDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        SongComment comment = songCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + commentId));

        if (!comment.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("User is not authorized to update this comment");
        }

        comment.setContent(commentDTO.getContent());
        SongComment updatedComment = songCommentRepository.save(comment);
        return toDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = getCurrentAuthenticatedUser();
        SongComment comment = songCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + commentId));

        if (!comment.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("User is not authorized to delete this comment");
        }

        Song song = comment.getSong();
        if (song != null) {
            song.setCommentCount(Math.max(0, song.getCommentCount() - 1));
            songRepository.save(song);
        }

        songCommentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public Page<SongCommentDTO> getPendingComments(Pageable pageable) {
        return songCommentRepository.findByStatus(CommentStatus.PENDING, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<SongCommentDTO> getPendingCommentsBySongId(Long songId, Pageable pageable) {
        User currentUser = getCurrentAuthenticatedUser();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new NoSuchElementException("Song not found with ID: " + songId));

        if (!song.getUploader().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("User is not authorized to view pending comments for this song");
        }
        return songCommentRepository.findBySongIdAndStatus(songId, CommentStatus.PENDING, pageable).map(this::toDTO);
    }

    @Transactional
    public void approveComment(Long commentId) {
        User currentUser = getCurrentAuthenticatedUser();
        SongComment comment = songCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + commentId));

        Song song = comment.getSong();
        // Check for ownership or admin role
        boolean isOwner = song.getUploader() != null && song.getUploader().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new SecurityException("User is not authorized to approve this comment");
        }

        comment.setStatus(CommentStatus.APPROVED);

        song.setCommentCount(song.getCommentCount() + 1);
        songRepository.save(song);

        if (song.getUploader() != null) {
            notificationService.createAndSendNotification(
                    comment.getUser(),                   // Sender
                    song.getUploader(),            // Recipient
                    NotificationType.COMMENT_SONG,    // Type
                    comment.getUser().getDisplayName() + " đã bình luận bài hát: " + song.getTitle(), // Message
                    song.getId()
            );
        }
        songCommentRepository.save(comment);
    }

    @Transactional
    public void rejectComment(Long commentId) {
        User currentUser = getCurrentAuthenticatedUser();
        SongComment comment = songCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + commentId));

        Song song = comment.getSong();
        // Check for ownership or admin role
        boolean isOwner = song.getUploader() != null && song.getUploader().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new SecurityException("User is not authorized to reject this comment");
        }

        comment.setStatus(CommentStatus.REJECTED);
        songCommentRepository.save(comment);
    }
}