package com.example.CMCmp3.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Sửa String -> Long

    @Column(nullable = false)
    private String title; // Sửa name -> title

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    // --- CÁC CỘT THỐNG KÊ (Phi chuẩn hóa) ---
    // Dùng Long thay vì int
    private Long playCount = 0L;    // Sửa listenCount -> playCount
    private Long likeCount = 0L;
    private Long commentCount = 0L;

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    @Column(nullable = false)
    private PlaylistPrivacy privacy = PlaylistPrivacy.PRIVATE; // Default to PRIVATE

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // --- CÁC MỐI QUAN HỆ ---

    // 1. Chủ sở hữu (ManyToOne)
    // Sửa user -> owner để rõ nghĩa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // 2. Danh sách bài hát (QUAN TRỌNG: Dùng bảng trung gian PlaylistSong để lưu thứ tự)
    // Không dùng @ManyToMany trực tiếp được vì cần cột 'position'
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistSong> playlistSongs = new HashSet<>();

    // 3. Quan hệ Likes và Comments (Để xóa playlist thì xóa luôn like/comment)
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlaylistComment> comments = new HashSet<>();
}