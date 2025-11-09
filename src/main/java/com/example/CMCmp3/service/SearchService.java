package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.ArtistDTO;
import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.dto.SearchResponseDTO;
import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.entity.Artist;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.ArtistRepository;
import com.example.CMCmp3.repository.PlaylistRepository;
import com.example.CMCmp3.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        response.setSongs(songRepository.searchSongsByTitleOrArtist(query)
                .stream()
                .map(this::toSongDTO)
                .collect(Collectors.toList()));

        response.setArtists(artistRepository.findAllByNameContainingIgnoreCase(query)
                .stream()
                .map(this::toArtistDTO)
                .collect(Collectors.toList()));

        List<Playlist> playlists = playlistRepository.findAllByNameContainingIgnoreCase(query);
        for (Playlist playlist : playlists) {
            playlist.getSongs().size();
        }
        response.setPlaylists(playlists
                .stream()
                .map(this::toPlaylistDTO)
                .collect(Collectors.toList()));

        return response;
    }

    private SongDTO toSongDTO(Song s) {
        SongDTO dto = new SongDTO();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        if (s.getArtist() != null) {
            dto.setArtist(s.getArtist().getName());
        }
        dto.setImageUrl(s.getImageUrl());
        dto.setFilePath(s.getFilePath());
        dto.setListenCount(s.getListenCount());
        dto.setLikeCount(s.getLikeCount());
        dto.setDescription(s.getDescription());
        dto.setLabel(s.getLabel());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }

    private ArtistDTO toArtistDTO(Artist a) {
        ArtistDTO dto = new ArtistDTO();
        dto.setId(a.getId());
        dto.setName(a.getName());
        dto.setImageUrl(a.getImageUrl());
        return dto;
    }

    private PlaylistDTO toPlaylistDTO(Playlist p) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setImageUrl(p.getImageUrl());
        if (p.getSongs() != null) {
            dto.setSongs(p.getSongs().stream().map(Song::getId).collect(Collectors.toSet()));
            dto.setNumberOfSongs(p.getSongs().size());
        } else {
            dto.setNumberOfSongs(0);
        }
        dto.setListenCount((long) p.getListenCount());
        dto.setLikeCount((long) p.getLikeCount());
        if (p.getUser() != null) {
            dto.setUserId(p.getUser().getId());
        }
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
