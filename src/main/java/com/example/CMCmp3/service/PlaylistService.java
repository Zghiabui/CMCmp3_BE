package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.CreatePlaylistDTO;
import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.dto.SongDTO;
import com.example.CMCmp3.dto.UpdatePlaylistDTO; // Import UpdatePlaylistDTO
import com.example.CMCmp3.dto.UpdatePlaylistSongsDTO;
import com.example.CMCmp3.entity.Playlist;
import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.entity.Song;
import com.example.CMCmp3.entity.PlaylistSong;
import com.example.CMCmp3.entity.PlaylistSongId;
import com.example.CMCmp3.repository.PlaylistRepository;
import com.example.CMCmp3.repository.UserRepository;
import com.example.CMCmp3.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;
import com.example.CMCmp3.entity.Role;
import com.example.CMCmp3.entity.PlaylistPrivacy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongService songService;
    private final SongRepository songRepository;

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

        // Tính số bài hát (thông qua bảng trung gian playlistSongs)
        if (p.getPlaylistSongs() != null) {
            dto.setSongCount(p.getPlaylistSongs().size());
            // Nếu muốn trả về list ID bài hát:
             dto.setSongs(p.getPlaylistSongs().stream().map(ps -> ps.getSong().getId()).collect(Collectors.toList()));
        } else {
            dto.setSongCount(0);
        }

        // Lấy tên chủ sở hữu (User)
        if (p.getOwner() != null) {
            dto.setOwnerName(p.getOwner().getDisplayName());
        }
        dto.setPrivacy(p.getPrivacy().name()); // Map privacy enum to String
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
                .orElseThrow(() -> new NoSuchElementException("Playlist not found"));
        return toDTO(p);
    }

    @Transactional(readOnly = true)
    public List<PlaylistDTO> findMyPlaylists() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return playlistRepository.findByOwner(currentUser).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getSongsByPlaylistId(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with ID: " + playlistId));

        // TODO: Add authorization check for private playlists

        return playlist.getPlaylistSongs().stream()
                .map(playlistSong -> songService.toDTO(playlistSong.getSong()))
                .collect(Collectors.toList());
    }

    @Transactional
    public PlaylistDTO updatePlaylist(Long playlistId, UpdatePlaylistDTO dto) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with ID: " + playlistId));

        // Authorization check
        if (!playlist.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You are not authorized to modify this playlist.");
        }

        playlist.setTitle(dto.getName());
        playlist.setPrivacy(PlaylistPrivacy.valueOf(dto.getPrivacy().toUpperCase()));

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return toDTO(updatedPlaylist);
    }

    @Transactional
    public List<SongDTO> updateSongsInPlaylist(Long playlistId, UpdatePlaylistSongsDTO dto) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found with ID: " + playlistId));

        // Authorization check
        if (!playlist.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You are not authorized to modify this playlist.");
        }

        Set<PlaylistSong> currentPlaylistSongs = playlist.getPlaylistSongs();

        // Handle additions
        if (dto.getAdd() != null && !dto.getAdd().isEmpty()) {
            for (Long songId : dto.getAdd()) {
                Song songToAdd = songRepository.findById(songId)
                        .orElseThrow(() -> new NoSuchElementException("Song not found with ID: " + songId));

                // Check for duplicates
                boolean alreadyExists = currentPlaylistSongs.stream()
                        .anyMatch(ps -> ps.getSong().getId().equals(songId));

                if (!alreadyExists) {
                    PlaylistSong newPlaylistSong = PlaylistSong.builder()
                            .id(new PlaylistSongId(playlistId, songId))
                            .playlist(playlist)
                            .song(songToAdd)
                            .order(currentPlaylistSongs.size() + 1) // Assign order
                            .build();
                    currentPlaylistSongs.add(newPlaylistSong);
                }
            }
        }

        // Handle removals
        if (dto.getRemove() != null && !dto.getRemove().isEmpty()) {
            currentPlaylistSongs.removeIf(ps -> dto.getRemove().contains(ps.getSong().getId()));
        }

        playlist.setPlaylistSongs(currentPlaylistSongs); // Update the set
        playlistRepository.save(playlist); // Save changes to the playlist and its songs

        return getSongsByPlaylistId(playlistId); // Return the updated list of songs
    }

    // Lấy Top Playlists (Tương tự như SongService)
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
        // Lấy User hiện tại đang đăng nhập
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist p = new Playlist();
        p.setTitle(dto.getName()); // Use dto.getName()
        p.setDescription(dto.getDescription());
        p.setImageUrl(dto.getImageUrl());
        p.setOwner(currentUser); // Gán chủ sở hữu
        p.setPlayCount(0L);
        p.setLikeCount(0L);
        p.setCommentCount(0L);
        p.setPrivacy(PlaylistPrivacy.valueOf(dto.getPrivacy().toUpperCase())); // Set privacy from DTO

        return toDTO(playlistRepository.save(p));
    }

    @Transactional
    public void deletePlaylist(Long id) {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Playlist not found"));

        // Check if current user is the owner or an ADMIN
        if (!playlist.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You are not authorized to delete this playlist.");
        }

        playlistRepository.deleteById(id);
    }
}