// src/main/java/com/example/CMCmp3/service/PlaylistQueryService.java
package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.TopPlaylistDTO;
import com.example.CMCmp3.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistQueryService {

    private final PlaylistRepository repo;

    @Transactional(readOnly = true)
    public List<TopPlaylistDTO> getTopPlaylists(int limit) {
        return repo.findTopByListenCount(PageRequest.of(0, Math.max(1, limit)));
    }
}
