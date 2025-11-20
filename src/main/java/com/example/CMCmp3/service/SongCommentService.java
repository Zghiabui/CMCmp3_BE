package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreateSongCommentDTO;
import com.example.CMCmp3.dto.SongCommentDTO;
import com.example.CMCmp3.dto.UpdateSongCommentDTO;
import com.example.CMCmp3.entity.Role;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.entity.SongComment;

import com.example.CMCmp3.entity.User;
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

        // Update comment count on the song
        song.setCommentCount(song.getCommentCount() + 1);
        songRepository.save(song);

        SongComment savedComment = songCommentRepository.save(newComment);
        return toDTO(savedComment);
    }

    @Transactional(readOnly = true)
    public Page<SongCommentDTO> getCommentsBySongId(Long songId, Pageable pageable) {
        if (!songRepository.existsById(songId)) {
            throw new NoSuchElementException("Song not found with ID: " + songId);
        }
        return songCommentRepository.findBySongId(songId, pageable).map(this::toDTO);
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
}
