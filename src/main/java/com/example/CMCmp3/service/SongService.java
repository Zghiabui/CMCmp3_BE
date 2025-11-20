package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.*;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.ArtistRepository;
import com.example.CMCmp3.repository.SongLikeRepository;
import com.example.CMCmp3.repository.SongRepository;
import com.example.CMCmp3.repository.TagRepository;
import com.example.CMCmp3.repository.UserRepository;
import com.mpatric.mp3agic.Mp3File;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final SongLikeRepository songLikeRepository;
    private final FirebaseStorageService firebaseStorageService;

    private User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));
    }

    // HELPERS

    private Set<Artist> fetchArtistsByIds(Set<Long> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(artistRepository.findAllById(artistIds));
    }

    private Set<Tag> fetchTagsByIds(Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(tagRepository.findAllById(tagIds));
    }

    private int calculateDuration(String filePath) {
        File tempFile = null;
        try {
            File fileToRead;
            if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
                tempFile = File.createTempFile("song_duration_calc_", ".mp3");
                try (InputStream in = new URL(filePath).openStream();
                     FileOutputStream out = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                fileToRead = tempFile;
            } else {
                fileToRead = new File(filePath);
            }

            if (fileToRead.exists()) {
                Mp3File mp3File = new Mp3File(fileToRead);
                return (int) mp3File.getLengthInSeconds();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Warning: Không thể tính duration cho file: " + filePath + ". Lỗi: " + e.getMessage());
            return 0;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    //  HELPERS

    public SongDTO toDTO(Song s) {
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

        if (s.getArtists() != null && !s.getArtists().isEmpty()) {
            dto.setArtistName(s.getArtists().stream()
                    .map(Artist::getName)
                    .collect(Collectors.joining(", ")));

            Set<ArtistDTO> artistDTOS = s.getArtists().stream()
                    .map(a -> {
                        ArtistDTO artistDto = new ArtistDTO();
                        artistDto.setId(a.getId());
                        artistDto.setName(a.getName());
                        artistDto.setImageUrl(a.getImageUrl());
                        return artistDto;
                    })
                    .collect(Collectors.toSet());
            dto.setArtists(artistDTOS);
        } else {
            dto.setArtists(Collections.emptySet());
            dto.setArtistName("");
        }

        if (s.getTags() != null) {
            Set<TagDTO> tagDTOS = s.getTags().stream()
                    .map(t -> {
                        TagDTO tDto = new TagDTO();
                        tDto.setId(t.getId());
                        tDto.setName(t.getName());
                        return tDto;
                    })
                    .collect(Collectors.toSet());
            dto.setTags(tagDTOS);
        } else {
            dto.setTags(Collections.emptySet());
        }

        if (s.getUploader() != null) {
            User uploader = s.getUploader();
            dto.setUploader(new SongDTO.UploaderDTO(uploader.getId(), uploader.getDisplayName()));
        }

        return dto;
    }

    private Song convertToEntity(CreateSongDTO dto) {
        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setFilePath(dto.getFilePath());
        song.setImageUrl(dto.getImageUrl());
        song.setDescription(dto.getDescription());

        // Giá trị mặc định
        song.setListenCount(0L);
        song.setLikeCount(0L);

        // lấy Entity từ ID
        song.setArtists(fetchArtistsByIds(dto.getArtistIds()));
        song.setTags(fetchTagsByIds(dto.getTagIds()));

        // tính Duration
        song.setDuration(calculateDuration(dto.getFilePath()));

        return song;
    }

    // READ

    @Transactional(readOnly = true)
    public Page<SongDTO> getAllSongs(Pageable pageable) {
        return songRepository.findAll(pageable).map(this::toDTO);
    }



    @Transactional(readOnly = true)
    public SongDTO getById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Song not found: " + id));
        return toDTO(song);
    }

    // TOP CHARTS

    @Transactional(readOnly = true)
    public List<SongDTO> getTopSongs(int limit) {
        return songRepository.findTopByListenCount(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getTopNewReleases(int limit) {
        return songRepository.findTopByCreatedAt(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getTopMostLiked(int limit) {
        return songRepository.findTopByLikeCount(PageRequest.of(0, limit))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getUploadedSongsForCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));
        List<Song> songs = songRepository.findByUploader(currentUser);
        return songs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getFavoriteSongsForCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));

        List<Song> likedSongs = songRepository.findLikedSongsByUserId(currentUser.getId());

        return likedSongs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getSongsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        List<Song> songs = songRepository.findByUploader(user);
        return songs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // WRITE

    @Transactional
    public SongDTO createSong(CreateSongDTO createSongDTO) {
        Song song = convertToEntity(createSongDTO);
        Song savedSong = songRepository.save(song);
        return toDTO(savedSong);
    }

    @Transactional
    public SongDTO createSongWithUpload(String title, String description, Set<Long> artistIds, Set<Long> tagIds, MultipartFile songFile, MultipartFile imageFile) {
        try {
            User currentUser = getCurrentAuthenticatedUser();

            // Store files (SỬ DỤNG FIREBASE)
            String songFilePath = firebaseStorageService.uploadFile(songFile);
            String imageFilePath = firebaseStorageService.uploadFile(imageFile);

            Song song = new Song();
            song.setTitle(title);
            song.setDescription(description);
            song.setFilePath(songFilePath);
            song.setImageUrl(imageFilePath);
            song.setUploader(currentUser);

            // Set default values và relationships
            song.setListenCount(0L);
            song.setLikeCount(0L);
            song.setArtists(fetchArtistsByIds(artistIds));
            song.setTags(fetchTagsByIds(tagIds));

            // Calculate duration
            song.setDuration(calculateDuration(songFilePath));

            // Save
            Song savedSong = songRepository.save(song);
            return toDTO(savedSong);

        } catch (IOException ex) {
            // Ném ra lỗi nếu Firebase upload thất bại
            throw new RuntimeException("Không thể upload file bài hát. Vui lòng thử lại!", ex);
        }
    }

    @Transactional
    public SongDTO updateUploadedSong(Long id,
                                      String title,
                                      String description,
                                      Set<Long> artistIds,
                                      Set<Long> tagIds,
                                      MultipartFile newSongFile,
                                      MultipartFile newImageFile) {

        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Song not found: " + id));

        User currentUser = getCurrentAuthenticatedUser();
        boolean isOwner = song.getUploader() != null && Objects.equals(song.getUploader().getId(), currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa bài hát này");
        }

        if (title != null && !title.isBlank()) {
            song.setTitle(title.trim());
        }
        if (description != null) {
            song.setDescription(description);
        }
        if (artistIds != null) {
            song.setArtists(fetchArtistsByIds(artistIds));
        }
        if (tagIds != null) {
            song.setTags(fetchTagsByIds(tagIds));
        }

        try {
            if (newSongFile != null && !newSongFile.isEmpty()) {
                String songFilePath = firebaseStorageService.uploadFile(newSongFile);
                song.setFilePath(songFilePath);
                song.setDuration(calculateDuration(songFilePath));
            }

            if (newImageFile != null && !newImageFile.isEmpty()) {
                String imageFilePath = firebaseStorageService.uploadFile(newImageFile);
                song.setImageUrl(imageFilePath);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Không thể cập nhật file bài hát. Vui lòng thử lại!", ex);
        }

        Song updatedSong = songRepository.save(song);
        return toDTO(updatedSong);
    }

    @Transactional
    public SongDTO updateSong(Long id, CreateSongDTO updateDTO) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Song not found: " + id));

        // Cập nhật thông tin
        if (updateDTO.getTitle() != null) song.setTitle(updateDTO.getTitle());
        if (updateDTO.getDescription() != null) song.setDescription(updateDTO.getDescription());
        if (updateDTO.getImageUrl() != null) song.setImageUrl(updateDTO.getImageUrl());

        // Cập nhật File & Duration
        if (updateDTO.getFilePath() != null && !updateDTO.getFilePath().equals(song.getFilePath())) {
            song.setFilePath(updateDTO.getFilePath());
            song.setDuration(calculateDuration(updateDTO.getFilePath()));
        }

        // Cập nhật Quan hệ
        if (updateDTO.getArtistIds() != null) {
            song.setArtists(fetchArtistsByIds(updateDTO.getArtistIds()));
        }
        if (updateDTO.getTagIds() != null) {
            song.setTags(fetchTagsByIds(updateDTO.getTagIds()));
        }

        Song updatedSong = songRepository.save(song);
        return toDTO(updatedSong);
    }

        @Transactional

        public void deleteSong(Long id) {

            if (!songRepository.existsById(id)) {

                throw new NoSuchElementException("Song not found: " + id);

            }

            songRepository.deleteById(id);

        }

        // LIKE/UNLIKE


        @Transactional

        public void likeSong(Long songId) {
            String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new NoSuchElementException("Song not found: " + songId));

            // 2. Check xem like chưa
            SongLikeId likeId = new SongLikeId(currentUser.getId(), song.getId());
            if (songLikeRepository.existsById(likeId)) {
                return;
            }

            // tạo like mới và up số like
            SongLike songLike = new SongLike(currentUser, song);
            songLikeRepository.save(songLike);
            song.setLikeCount(song.getLikeCount() + 1);
            songRepository.save(song);
        }

        @Transactional
        public void unlikeSong(Long songId) {
            String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new NoSuchElementException("Song not found: " + songId));

            SongLikeId likeId = new SongLikeId(currentUser.getId(), song.getId());
            SongLike songLike = songLikeRepository.findById(likeId)
                    .orElse(null); // Find the like to delete

            //If like exists, delete it and update count
            if (songLike != null) {
                songLikeRepository.delete(songLike);
                        song.setLikeCount(Math.max(0, song.getLikeCount() - 1));
                        songRepository.save(song);
                    }
                }

                @Transactional
                public void incrementListenCount(Long songId) {
                    Song song = songRepository.findById(songId)
                            .orElseThrow(() -> new NoSuchElementException("Song not found: " + songId));
                    song.setListenCount(song.getListenCount() + 1);
                    songRepository.save(song);
                }
                }

    