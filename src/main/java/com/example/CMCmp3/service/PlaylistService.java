package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreatePlaylistDTO;
import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.repository.PlaylistRepository;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    // --- MAPPING ---
    private PlaylistDTO toDTO(Playlist p) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setImageUrl(p.getImageUrl());
        dto.setPlayCount(p.getPlayCount());
        dto.setLikeCount(p.getLikeCount());
        dto.setCreatedAt(p.getCreatedAt());

        // Tính số bài hát (thông qua bảng trung gian playlistSongs)
        if (p.getPlaylistSongs() != null) {
            dto.setSongCount(p.getPlaylistSongs().size());
            // Nếu muốn trả về list ID bài hát:
            // dto.setSongIds(p.getPlaylistSongs().stream().map(ps -> ps.getSong().getId()).collect(Collectors.toSet()));
        } else {
            dto.setSongCount(0);
        }

        // Lấy tên chủ sở hữu (User)
        if (p.getOwner() != null) {
            dto.setOwnerName(p.getOwner().getDisplayName());
        }
        return dto;
    }

    // --- LOGIC ---

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getAll() {
        return playlistRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlaylistDTO getById(Long id) {
        Playlist p = playlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found"));
        return toDTO(p);
    }

    // Lấy Top Playlists (Tương tự như SongService)
    @Transactional(readOnly = true)
    public List<PlaylistDTO> getTopPlaylistsByPlayCount(int limit) {
        return playlistRepository.findTopByPlayCount(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getTopPlaylistsByLikeCount(int limit) {
        return playlistRepository.findTopByLikeCount(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getTopNewPlaylists(int limit) {
        return playlistRepository.findTopByCreatedAt(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public PlaylistDTO createPlaylist(CreatePlaylistDTO dto) {
        // Lấy User hiện tại đang đăng nhập
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist p = new Playlist();
        p.setTitle(dto.getTitle());
        p.setDescription(dto.getDescription());
        p.setImageUrl(dto.getImageUrl());
        p.setOwner(currentUser); // Gán chủ sở hữu
        p.setPlayCount(0L);
        p.setLikeCount(0L);
        p.setCommentCount(0L);

        return toDTO(playlistRepository.save(p));
    }

    @Transactional
    public void deletePlaylist(Long id) {
        // TODO: Kiểm tra quyền (chỉ owner hoặc admin mới được xóa)
        playlistRepository.deleteById(id);
    }
}