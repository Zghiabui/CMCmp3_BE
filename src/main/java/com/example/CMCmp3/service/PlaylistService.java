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

    private static Long toLong(Number n, long def) {
        return (n == null) ? def : n.longValue();
    }

    private PlaylistDTO toDTO(Playlist p) {
        // Giữ String vì Song.id là String/UUID
        Set<String> songIds = Optional.ofNullable(p.getSongs())
                .orElse(Collections.emptySet())
                .stream()
                .map(Song::getId)
                .collect(Collectors.toSet());

        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(p.getId());                           // String -> String
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setImageUrl(p.getImageUrl());

        dto.setSongs(songIds);
        dto.setNumberOfSongs(songIds.size());

        // Entity đang int -> convert null-safe sang Long cho DTO
        dto.setListenCount(toLong(p.getListenCount(), 0));
        dto.setLikeCount(toLong(p.getLikeCount(), 0));

        dto.setUserId(p.getUser() != null ? p.getUser().getId() : null);
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
