package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreatePlaylistCommentDTO;
import com.example.CMCmp3.dto.PlaylistCommentDTO;
import com.example.CMCmp3.dto.UpdatePlaylistCommentDTO;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.PlaylistComment;
import com.example.CMCmp3.entity.Role;
import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.repository.PlaylistCommentRepository;
import com.example.CMCmp3.repository.PlaylistRepository;
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
public class PlaylistCommentService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final PlaylistCommentRepository playlistCommentRepository;

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

    private PlaylistCommentDTO toDTO(PlaylistComment comment) {
        return new PlaylistCommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                PlaylistCommentDTO.fromUser(comment.getUser())
        );
    }

    @Transactional
    public PlaylistCommentDTO addCommentToPlaylist(Long playlistId, CreatePlaylistCommentDTO commentDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with ID: " + playlistId));

        PlaylistComment newComment = PlaylistComment.builder()
                .playlist(playlist)
                .user(currentUser)
                .content(commentDTO.getContent())
                .build();

        long currentCommentCount = playlist.getCommentCount() != null ? playlist.getCommentCount() : 0L;
        playlist.setCommentCount(currentCommentCount + 1);
        playlistRepository.save(playlist);

        PlaylistComment savedComment = playlistCommentRepository.save(newComment);
        return toDTO(savedComment);
    }

    @Transactional(readOnly = true)
    public Page<PlaylistCommentDTO> getCommentsByPlaylistId(Long playlistId, Pageable pageable) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new NoSuchElementException("Playlist not found with ID: " + playlistId);
        }
        return playlistCommentRepository.findByPlaylistId(playlistId, pageable).map(this::toDTO);
    }

    @Transactional
    public PlaylistCommentDTO updateComment(Long commentId, UpdatePlaylistCommentDTO commentDTO) {
        User currentUser = getCurrentAuthenticatedUser();
        PlaylistComment comment = playlistCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + commentId));

        if (!comment.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("User is not authorized to update this comment");
        }

        comment.setContent(commentDTO.getContent());
        PlaylistComment updatedComment = playlistCommentRepository.save(comment);
        return toDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = getCurrentAuthenticatedUser();
        PlaylistComment comment = playlistCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with ID: " + commentId));

        if (!comment.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("User is not authorized to delete this comment");
        }

        Playlist playlist = comment.getPlaylist();
        if (playlist != null) {
            long currentCommentCount = playlist.getCommentCount() != null ? playlist.getCommentCount() : 0L;
            playlist.setCommentCount(Math.max(0, currentCommentCount - 1));
            playlistRepository.save(playlist);
        }

        playlistCommentRepository.delete(comment);
    }
}
