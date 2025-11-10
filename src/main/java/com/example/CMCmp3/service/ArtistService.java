package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.ArtistDTO;
import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.entity.Artist;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.ArtistRepository;
import com.example.CMCmp3.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    @Transactional(readOnly = true)
    public ArtistDTO getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Artist not found: " + id));
        return toDTO(artist);
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getSongsByArtistId(Long id) {
        return songRepository.findAllByArtistId(id)
                .stream()
                .map(this::toSongDTO)
                .collect(Collectors.toList());
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

    private ArtistDTO toDTO(Artist a) {
        ArtistDTO dto = new ArtistDTO();
        dto.setId(a.getId());
        dto.setName(a.getName());
        dto.setImageUrl(a.getImageUrl());
        return dto;
    }
}
