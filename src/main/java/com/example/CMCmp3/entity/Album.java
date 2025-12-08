package com.example.CMCmp3.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "albums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private Long playCount = 0L;
    private Long likeCount = 0L;
    private Long commentCount = 0L;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // --- CÁC MỐI QUAN HỆ ---

    // Chủ sở hữu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // Danh sách bài hát (QUAN TRỌNG: Dùng bảng trung gian AlbumSong để lưu thứ tự)
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlbumSong> albumSongs = new HashSet<>();

    // Quan hệ Likes và Comments (Để xóa album thì xóa luôn like/comment)
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlbumLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlbumComment> comments = new HashSet<>();

    // Danh sách nghệ sĩ liên quan (ManyToMany)
    @ManyToMany
    @JoinTable(
            name = "album_artists",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists = new HashSet<>();
}
