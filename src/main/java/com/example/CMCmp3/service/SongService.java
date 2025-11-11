package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.*;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.ArtistRepository;
import com.example.CMCmp3.repository.SongRepository;
import com.example.CMCmp3.repository.TagRepository;
import com.example.CMCmp3.repository.UserRepository;
import com.example.CMCmp3.repository.SongLikeRepository;
import com.mpatric.mp3agic.Mp3File;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final TagRepository tagRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final SongLikeRepository songLikeRepository;

    private static final String BASE_URL = "http://localhost:8080/"; // Or configure via application.properties

    // =================================================================
    // 1. HELPERS: FETCHING & CALCULATION (Logic phụ trợ)
    // =================================================================

    /**
     * Helper: Lấy danh sách Artist Entity từ Set<Long> ID
     */
    private Set<Artist> fetchArtistsByIds(Set<Long> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(artistRepository.findAllById(artistIds));
    }

    /**
     * Helper: Lấy danh sách Tag Entity từ Set<Long> ID
     */
    private Set<Tag> fetchTagsByIds(Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(tagRepository.findAllById(tagIds));
    }

    /**
     * Helper: Tính Duration từ file MP3 (Local hoặc URL)
     * Sử dụng thư viện mp3agic
     */
    private int calculateDuration(String filePath) {
        File tempFile = null;
        try {
            File fileToRead;
            // Nếu là URL (Firebase/Cloud), phải tải về file tạm mới đọc được metadata
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
                // Nếu là đường dẫn local
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
            // Quan trọng: Xóa file tạm sau khi dùng xong
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    // =================================================================
    // 2. HELPERS: MAPPERS (Chuyển đổi dữ liệu)
    // =================================================================

    /**
     * Convert: Entity -> DTO (Response)
     */
    private SongDTO toDTO(Song s) {
        SongDTO dto = new SongDTO();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setDuration(s.getDuration());
        dto.setFilePath(s.getFilePath()); // FilePath can remain relative as it's for internal use or streaming
        
        // Construct full URL for image
        if (s.getImageUrl() != null && !s.getImageUrl().isEmpty()) {
            if (s.getImageUrl().startsWith("http://") || s.getImageUrl().startsWith("https://")) {
                dto.setImageUrl(s.getImageUrl()); // Already a full URL
            } else {
                dto.setImageUrl(BASE_URL + s.getImageUrl()); // Prepend base URL for relative paths
            }
        } else {
            dto.setImageUrl(null); // Or a default image URL
        }
        
        dto.setListenCount(s.getListenCount());
        dto.setLikeCount(s.getLikeCount());
        dto.setDescription(s.getDescription());
        dto.setCreatedAt(s.getCreatedAt());

        // Map danh sách Ca sĩ
        if (s.getArtists() != null) {
            Set<ArtistDTO> artistDTOS = s.getArtists().stream()
                    .map(a -> new ArtistDTO(a.getId(), a.getName(), a.getImageUrl(), a.getSongCount()))
                    .collect(Collectors.toSet());
            dto.setArtists(artistDTOS);
        } else {
            dto.setArtists(Collections.emptySet());
        }

        // Map danh sách Thể loại (Tag)
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

        return dto;
    }

    /**
     * Convert: CreateDTO -> Entity (Dùng khi tạo mới)
     */
    private Song convertToEntity(CreateSongDTO dto) {
        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setFilePath(dto.getFilePath());
        song.setImageUrl(dto.getImageUrl());
        song.setDescription(dto.getDescription());

        // Giá trị mặc định
        song.setListenCount(0L);
        song.setLikeCount(0L);

        // Gọi Helper để lấy Entity từ ID
        song.setArtists(fetchArtistsByIds(dto.getArtistIds()));
        song.setTags(fetchTagsByIds(dto.getTagIds()));

        // Gọi Helper tính Duration
        song.setDuration(calculateDuration(dto.getFilePath()));

        return song;
    }

    // =================================================================
    // 3. READ OPERATIONS (Đọc dữ liệu)
    // =================================================================

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

    @Transactional(readOnly = true)
    public Resource getSongFile(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Song not found: " + id));
        // Use FileStorageService to correctly load the file as a resource
        return fileStorageService.loadFileAsResource(song.getFilePath());
    }

    // --- TOP CHARTS (Sử dụng logic Repository trả về List Entity) ---

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
        // 1. Get current user
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));

        // 2. Find songs by uploader
        List<Song> songs = songRepository.findByUploader(currentUser);

        // 3. Map to DTOs and return
        return songs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SongDTO> getFavoriteSongsForCurrentUser() {
        // 1. Get current user
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));

        // 2. Get liked songs from the user entity
        return currentUser.getLikedSongs().stream()
                .map(SongLike::getSong) // Extract Song from SongLike
                .map(this::toDTO)      // Map Song to SongDTO
                .collect(Collectors.toList());
    }

    // =================================================================
    // 4. WRITE OPERATIONS (Ghi dữ liệu)
    // =================================================================

    @Transactional
    public SongDTO createSong(CreateSongDTO createSongDTO) {
        Song song = convertToEntity(createSongDTO);
        Song savedSong = songRepository.save(song);
        return toDTO(savedSong);
    }

    @Transactional
    public SongDTO createSongWithUpload(String title, String description, Set<Long> artistIds, Set<Long> tagIds, MultipartFile songFile, MultipartFile imageFile) {
        // 0. Get current user
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));

        // 1. Store files
        String songFilePath = fileStorageService.storeFile(songFile, "music");
        String imageFilePath = fileStorageService.storeFile(imageFile, "images");

        // 2. Create new Song entity
        Song song = new Song();
        song.setTitle(title);
        song.setDescription(description);
        song.setFilePath(songFilePath);
        song.setImageUrl(imageFilePath);
        song.setUploader(currentUser); // Set the uploader

        // 3. Set default values and relationships
        song.setListenCount(0L);
        song.setLikeCount(0L);
        song.setArtists(fetchArtistsByIds(artistIds));
        song.setTags(fetchTagsByIds(tagIds));

        // 4. Calculate duration from the stored file
        // The file path is now relative, so we need to resolve it against the root upload directory
        String fullSongPath = Paths.get("uploads").resolve(songFilePath).toAbsolutePath().toString();
        song.setDuration(calculateDuration(fullSongPath));

        // 5. Save and return DTO
        Song savedSong = songRepository.save(song);
        return toDTO(savedSong);
    }

    @Transactional
    public SongDTO updateSong(Long id, CreateSongDTO updateDTO) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Song not found: " + id));

        // 1. Cập nhật thông tin cơ bản (Partial Update - check null)
        if (updateDTO.getTitle() != null) song.setTitle(updateDTO.getTitle());
        if (updateDTO.getDescription() != null) song.setDescription(updateDTO.getDescription());
        if (updateDTO.getImageUrl() != null) song.setImageUrl(updateDTO.getImageUrl());

        // 2. Cập nhật File & Duration (Nếu file thay đổi thì tính lại duration)
        if (updateDTO.getFilePath() != null && !updateDTO.getFilePath().equals(song.getFilePath())) {
            song.setFilePath(updateDTO.getFilePath());
            song.setDuration(calculateDuration(updateDTO.getFilePath()));
        }

        // 3. Cập nhật Quan hệ (Sử dụng Helper)
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
}