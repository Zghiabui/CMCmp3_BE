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

    // 1. HELPERS: FETCHING & CALCULATION (Logic phụ trợ)

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
    public SongDTO toDTO(Song s) {
        SongDTO dto = new SongDTO();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setDuration(s.getDuration());
        dto.setFilePath(s.getFilePath()); // Đây sẽ là URL Firebase của file MP3
        dto.setImageUrl(s.getImageUrl()); // Đây sẽ là URL Firebase của file ảnh
        // ... (Giữ nguyên phần còn lại của hàm)

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

        // 2. Get liked songs using the new repository method
        List<Song> likedSongs = songRepository.findLikedSongsByUserId(currentUser.getId());

        // 3. Map to DTOs and return
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

    // 4. WRITE OPERATIONS (Ghi dữ liệu)

    @Transactional
    public SongDTO createSong(CreateSongDTO createSongDTO) {
        Song song = convertToEntity(createSongDTO);
        Song savedSong = songRepository.save(song);
        return toDTO(savedSong);
    }

    @Transactional
    public SongDTO createSongWithUpload(String title, String description, Set<Long> artistIds, Set<Long> tagIds, MultipartFile songFile, MultipartFile imageFile) {
        try {
            // 0. Get current user
            User currentUser = getCurrentAuthenticatedUser();

            // 1. Store files (SỬ DỤNG FIREBASE)
            String songFilePath = firebaseStorageService.uploadFile(songFile); // <-- SỬA LẠI
            String imageFilePath = firebaseStorageService.uploadFile(imageFile); // <-- SỬA LẠI

            // 2. Create new Song entity
            Song song = new Song();
            song.setTitle(title);
            song.setDescription(description);
            song.setFilePath(songFilePath); // <-- URL từ Firebase
            song.setImageUrl(imageFilePath); // <-- URL từ Firebase
            song.setUploader(currentUser); // Set the uploader

            // 3. Set default values and relationships
            song.setListenCount(0L);
            song.setLikeCount(0L);
            song.setArtists(fetchArtistsByIds(artistIds));
            song.setTags(fetchTagsByIds(tagIds));

            // 4. Calculate duration (HÀM calculateDuration SẼ TỰ XỬ LÝ URL)
            // String fullSongPath = Paths.get("uploads").resolve(songFilePath)... // <-- XÓA DÒNG CŨ
            song.setDuration(calculateDuration(songFilePath)); // <-- TRUYỀN THẲNG URL VÀO

            // 5. Save and return DTO
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

    

        // =================================================================

        // 5. LIKE/UNLIKE OPERATIONS

        // =================================================================

    

        @Transactional

        public void likeSong(Long songId) {

            // 1. Get current user and song

            String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            User currentUser = userRepository.findByEmail(email)

                    .orElseThrow(() -> new RuntimeException("Current user not found"));

            Song song = songRepository.findById(songId)

                    .orElseThrow(() -> new NoSuchElementException("Song not found: " + songId));

    

            // 2. Check if already liked

            SongLikeId likeId = new SongLikeId(currentUser.getId(), song.getId());

            if (songLikeRepository.existsById(likeId)) {

                // Optional: throw an exception or just return

                return; // Already liked, do nothing

            }

    

            // 3. Create new like and update count

            SongLike songLike = new SongLike(currentUser, song);

            songLikeRepository.save(songLike);

    

            song.setLikeCount(song.getLikeCount() + 1);

            songRepository.save(song);

        }

    

        @Transactional

        public void unlikeSong(Long songId) {

            // 1. Get current user and song

            String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            User currentUser = userRepository.findByEmail(email)

                    .orElseThrow(() -> new RuntimeException("Current user not found"));

            Song song = songRepository.findById(songId)

                    .orElseThrow(() -> new NoSuchElementException("Song not found: " + songId));

    

            // 2. Find the like

            SongLikeId likeId = new SongLikeId(currentUser.getId(), song.getId());

            SongLike songLike = songLikeRepository.findById(likeId)

                    .orElse(null); // Find the like to delete

    

            // 3. If like exists, delete it and update count

            if (songLike != null) {

                songLikeRepository.delete(songLike);

    

                        song.setLikeCount(Math.max(0, song.getLikeCount() - 1)); // Avoid negative counts

    

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

    