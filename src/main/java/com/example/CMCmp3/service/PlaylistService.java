package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.dto.TopPlaylistDTO;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getAllPlaylists(Sort sort) {
        return playlistRepository.findAll(sort)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlaylistDTO getPlaylistById(String id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found: " + id));
        return toDTO(playlist);
    }

    @Transactional(readOnly = true)
    public List<TopPlaylistDTO> getTopPlaylists(int limit) {
        return playlistRepository.findTopByListenCount(PageRequest.of(0, Math.max(1, limit)));
    }

    @Transactional(readOnly = true)
    public List<TopPlaylistDTO> getTopPlaylistsByReleaseDate(int limit) {
        return playlistRepository.findTopByCreatedAt(PageRequest.of(0, Math.max(1, limit)));
    }

    @Transactional(readOnly = true)
    public List<TopPlaylistDTO> getTopPlaylistsByLikes(int limit) {
        return playlistRepository.findTopByLikeCount(PageRequest.of(0, Math.max(1, limit)));
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

        dto.setSongs(songIds);                           // ✅ Set<String>
        dto.setNumberOfSongs(songIds.size());            // ✅ Tự tính
        dto.setListenCount(Long.valueOf(p.getListenCount()));
        dto.setLikeCount(Long.valueOf(p.getLikeCount()));
        dto.setUserId(p.getUser() != null ? p.getUser().getId() : null);
        dto.setCreatedAt(p.getCreatedAt());              // ✅ Hiển thị yyyy-MM-dd nhờ @JsonFormat

        return dto;
    }
}
