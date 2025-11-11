package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.*;
import com.example.CMCmp3.entity.Artist;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.ArtistRepository;
import com.example.CMCmp3.repository.PlaylistRepository;
import com.example.CMCmp3.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final PlaylistRepository playlistRepository;

    @Transactional(readOnly = true)
    public SearchResponseDTO search(String query) {
        SearchResponseDTO response = new SearchResponseDTO();

        // 1. Tìm bài hát (Theo tiêu đề hoặc tên ca sĩ)
        List<SongDTO> songs = songRepository.searchSongsByTitleOrArtist(query)
                .stream()
                .map(this::toSongDTO)
                .collect(Collectors.toList());
        response.setSongs(songs);

        // 2. Tìm ca sĩ (Theo tên)
        List<ArtistDTO> artists = artistRepository.searchByName(query)
                .stream()
                .map(this::toArtistDTO)
                .collect(Collectors.toList());
        response.setArtists(artists);

        // 3. Tìm Playlist (Theo tiêu đề)
        List<PlaylistDTO> playlists = playlistRepository.searchByTitle(query)
                .stream()
                .map(this::toPlaylistDTO)
                .collect(Collectors.toList());
        response.setPlaylists(playlists);

        return response;
    }

    // --- PRIVATE MAPPERS (Đồng bộ logic với các Service khác) ---

    // Mapping Song Entity -> DTO
    private SongDTO toSongDTO(Song s) {
        SongDTO dto = new SongDTO();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setDuration(s.getDuration());
        dto.setFilePath(s.getFilePath());
        dto.setImageUrl(s.getImageUrl());
        dto.setListenCount(s.getListenCount());
        dto.setLikeCount(s.getLikeCount());
        dto.setDescription(s.getDescription());
        dto.setCreatedAt(s.getCreatedAt());

        // Map danh sách Artists (Thay vì getArtist đơn lẻ)
        if (s.getArtists() != null) {
            dto.setArtists(s.getArtists().stream()
                    .map(this::toArtistDTO) // Tái sử dụng hàm toArtistDTO
                    .collect(Collectors.toSet()));
        } else {
            dto.setArtists(Collections.emptySet());
        }

        // Map danh sách Tags (Thay vì label)
        if (s.getTags() != null) {
            dto.setTags(s.getTags().stream()
                    .map(t -> new TagDTO(t.getId(), t.getName(), t.getDescription()))
                    .collect(Collectors.toSet()));
        } else {
            dto.setTags(Collections.emptySet());
        }

        return dto;
    }

    // Mapping Artist Entity -> DTO
    private ArtistDTO toArtistDTO(Artist a) {
        return new ArtistDTO(a.getId(), a.getName(), a.getImageUrl(), a.getSongCount());
    }

    // Mapping Playlist Entity -> DTO
    private PlaylistDTO toPlaylistDTO(Playlist p) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle()); // Sửa name -> title
        dto.setDescription(p.getDescription());
        dto.setImageUrl(p.getImageUrl());
        dto.setPlayCount(p.getPlayCount()); // Sửa listenCount -> playCount
        dto.setLikeCount(p.getLikeCount());
        dto.setCreatedAt(p.getCreatedAt());

        // Tính số lượng bài hát thông qua bảng trung gian PlaylistSong
        if (p.getPlaylistSongs() != null) {
            dto.setSongCount(p.getPlaylistSongs().size());
        } else {
            dto.setSongCount(0);
        }

        // Lấy tên người tạo (User -> Owner)
        if (p.getOwner() != null) {
            dto.setOwnerName(p.getOwner().getDisplayName());
        }

        return dto;
    }
}