package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    /* ========= READ ========= */

    @Transactional(readOnly = true)
    public List<SongDTO> getAll() {
        return songRepository.findAll()
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

    /* ========= MAPPING (Entity -> DTO) ========= */

    private SongDTO toDTO(Song s) {
        SongDTO dto = new SongDTO();

        // Entity.id (String) -> DTO.id (Long)
        dto.setId(parseLongSafe(s.getId())); // null nếu id không phải số

        dto.setTitle(s.getTitle());
        dto.setArtist(s.getArtist());
        dto.setDuration(s.getDuration() == null ? 0 : s.getDuration());
        dto.setImageUrl(s.getImageUrl());
        dto.setFilePath(s.getFilePath());
        dto.setListenCount(s.getListenCount());
        dto.setLikeCount(s.getLikeCount());
        dto.setDescription(s.getDescription());
        dto.setLabel(s.getLabel());

        return dto;
    }

    private Long parseLongSafe(String val) {
        if (val == null) return null;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
