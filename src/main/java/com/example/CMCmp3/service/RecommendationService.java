package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.SongListenLogRepository;
import com.example.CMCmp3.repository.SongRepository;
import com.example.CMCmp3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private SongListenLogRepository songListenLogRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongService songService;

    public List<SongDTO> getPersonalizedRecommendations(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 1. Get user's listening history
        List<SongListenLog> listenLogs = songListenLogRepository.findByUser(user);

        // If no history, return top 10 popular songs
        if (listenLogs.isEmpty()) {
            return songRepository.findTop10ByOrderByListenCountDesc().stream()
                    .map(song -> songService.mapToSongDto(song, user))
                    .collect(Collectors.toList());
        }

        Set<Long> listenedSongIds = listenLogs.stream()
                .map(log -> log.getSong().getId())
                .collect(Collectors.toSet());

        // 2. Find all artists and tags from listened songs
        Set<Long> artistIds = listenLogs.stream()
                .flatMap(log -> log.getSong().getArtists().stream())
                .map(Artist::getId)
                .collect(Collectors.toSet());

        Set<Long> tagIds = listenLogs.stream()
                .flatMap(log -> log.getSong().getTags().stream())
                .map(Tag::getId)
                .collect(Collectors.toSet());
        
        if (artistIds.isEmpty() && tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Find similar songs based on these artists and tags
        List<Song> recommendedSongs = songRepository.findSongsByArtistsAndTags(artistIds, tagIds);

        // 4. Filter out already listened songs, ensure distinct, limit the result, and map to DTO
        return recommendedSongs.stream()
                .filter(song -> !listenedSongIds.contains(song.getId()))
                .distinct()
                .limit(10) // Limit to 10 recommendations
                .map(song -> songService.mapToSongDto(song, user))
                .collect(Collectors.toList());
    }
}
