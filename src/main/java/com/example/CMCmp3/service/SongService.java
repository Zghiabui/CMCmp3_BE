package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.dto.TopSongDTO;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    /* ========= READ ========= */

    @Transactional(readOnly = true)
    public List<SongDTO> getAll(org.springframework.data.domain.Sort sort) {
        return songRepository.findAll(sort)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SongDTO getById(String id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Song not found: " + id));
        return toDTO(song);
    }

    @Transactional(readOnly = true)
    public List<TopSongDTO> getTopSongs(int limit) {
        return songRepository.findTopByListenCount(PageRequest.of(0, Math.max(1, limit)));
    }

    @Transactional(readOnly = true)
    public List<TopSongDTO> getTopSongsByReleaseDate(int limit) {
        return songRepository.findTopByCreatedAt(PageRequest.of(0, Math.max(1, limit)));
    }

    @Transactional(readOnly = true)
    public List<TopSongDTO> getTopSongsByLikes(int limit) {
        return songRepository.findTopByLikeCount(PageRequest.of(0, Math.max(1, limit)));
    }

    @Transactional(readOnly = true)
    public Resource getSongFile(String id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Song not found: " + id));

        String filePath = song.getFilePath();
        try {
            Path file = Paths.get(filePath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    /* ========= MAPPING (Entity -> DTO) ========= */

    private SongDTO toDTO(Song s) {
        SongDTO dto = new SongDTO();

        // Entity.id (String) -> DTO.id (String)
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


}
