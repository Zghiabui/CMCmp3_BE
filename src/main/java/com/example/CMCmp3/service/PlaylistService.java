package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getAllPlaylists() {
        return playlistRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PlaylistDTO toDTO(Playlist p) {

        // ✅ Lấy danh sách ID bài hát và ép String → Long để khớp PlaylistDTO
        Set<Long> songIds = Optional.ofNullable(p.getSongs())
                .orElse(Collections.emptySet())
                .stream()
                .map(Song::getId)              // Song.id là String
                .map(Long::valueOf)            // → ép sang Long theo DTO
                .collect(Collectors.toSet());

        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setImageUrl(p.getImageUrl());
        dto.setSongs(songIds);                           // ✅ Set<Long>
        dto.setNumberOfSongs(songIds.size());            // ✅ Tự tính
        dto.setListenCount(Long.valueOf(p.getListenCount()));
        dto.setLikeCount(Long.valueOf(p.getLikeCount()));
        dto.setUserId(p.getUser() != null ? p.getUser().getId() : null);
        dto.setCreatedAt(p.getCreatedAt());              // ✅ Hiển thị yyyy-MM-dd nhờ @JsonFormat

        return dto;
    }
}
