package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreatePlaylistDTO;
import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.dto.SongsToPlaylistDTO;
import com.example.CMCmp3.dto.UpdatePlaylistDTO;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.PlaylistSong;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.repository.PlaylistRepository;
import com.example.CMCmp3.repository.PlaylistSongRepository;
import com.example.CMCmp3.repository.SongRepository;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistSongRepository playlistSongRepository;


    // --- HELPERS ---
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found in database"));
    }

    private void checkOwnership(Playlist playlist, User user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (!isAdmin && !Objects.equals(playlist.getOwner().getId(), user.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this playlist.");
        }
    }

    // --- MAPPING ---
    private PlaylistDTO toDTO(Playlist p) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setImageUrl(p.getImageUrl());
        dto.setPlayCount(p.getPlayCount());
        dto.setLikeCount(p.getLikeCount());
        dto.setCreatedAt(p.getCreatedAt());

        if (p.getPlaylistSongs() != null) {
            dto.setSongCount(p.getPlaylistSongs().size());
            dto.setSongs(p.getPlaylistSongs().stream()
                    .map(ps -> ps.getSong().getId())
                    .collect(Collectors.toList()));
        } else {
            dto.setSongCount(0);
        }

        if (p.getOwner() != null) {
            dto.setOwnerName(p.getOwner().getDisplayName());
        }
        return dto;
    }

    // --- LOGIC ---

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getAll() {
        return playlistRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlaylistDTO getById(Long id) {
        Playlist p = playlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with id: " + id));
        return toDTO(p);
    }

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getTopPlaylistsByPlayCount(int limit) {
        return playlistRepository.findTopByPlayCount(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getTopPlaylistsByLikeCount(int limit) {
        return playlistRepository.findTopByLikeCount(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlaylistDTO> getTopNewPlaylists(int limit) {
        return playlistRepository.findTopByCreatedAt(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public PlaylistDTO createPlaylist(CreatePlaylistDTO dto) {
        User currentUser = getCurrentUser();

        Playlist p = new Playlist();
        p.setTitle(dto.getTitle());
        p.setDescription(dto.getDescription());
        p.setImageUrl(dto.getImageUrl());
        p.setOwner(currentUser);
        p.setPlayCount(0L);
        p.setLikeCount(0L);
        p.setCommentCount(0L);

        return toDTO(playlistRepository.save(p));
    }

    @Transactional
    public PlaylistDTO updatePlaylist(Long id, UpdatePlaylistDTO dto) {
        User currentUser = getCurrentUser();
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with id: " + id));

        checkOwnership(playlist, currentUser);

        if (StringUtils.hasText(dto.getTitle())) {
            playlist.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            playlist.setDescription(dto.getDescription());
        }
        if (dto.getImageUrl() != null) {
            playlist.setImageUrl(dto.getImageUrl());
        }

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return toDTO(updatedPlaylist);
    }

    @Transactional
    public PlaylistDTO addSongsToPlaylist(Long playlistId, SongsToPlaylistDTO dto) {
        User currentUser = getCurrentUser();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with id: " + playlistId));

        checkOwnership(playlist, currentUser);

        List<Song> songsToAdd = songRepository.findAllById(dto.getSongIds());
        if (songsToAdd.size() != dto.getSongIds().size()) {
            throw new NoSuchElementException("One or more songs not found.");
        }

        int currentMaxPosition = playlist.getPlaylistSongs().size();
        for (Song song : songsToAdd) {
            // Tránh thêm trùng
            boolean alreadyExists = playlist.getPlaylistSongs().stream()
                    .anyMatch(ps -> ps.getSong().getId().equals(song.getId()));
            if (!alreadyExists) {
                PlaylistSong playlistSong = new PlaylistSong(playlist, song, currentMaxPosition++);
                playlist.getPlaylistSongs().add(playlistSong);
            }
        }

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return toDTO(updatedPlaylist);
    }

    @Transactional
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        User currentUser = getCurrentUser();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with id: " + playlistId));

        checkOwnership(playlist, currentUser);

        PlaylistSong playlistSong = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(() -> new NoSuchElementException("Song with id " + songId + " not found in playlist with id " + playlistId));

        // Xóa khỏi list và orphanRemoval=true sẽ xóa khỏi DB
        playlist.getPlaylistSongs().remove(playlistSong);
        
        // Cập nhật lại order của các bài hát còn lại
        for (int i = 0; i < playlist.getPlaylistSongs().size(); i++) {
            playlist.getPlaylistSongs().get(i).setOrder(i);
        }
        
        playlistRepository.save(playlist);
    }


    @Transactional
    public void deletePlaylist(Long id) {
        User currentUser = getCurrentUser();
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with id: " + id));

        checkOwnership(playlist, currentUser);

        playlistRepository.delete(playlist);
    }
}