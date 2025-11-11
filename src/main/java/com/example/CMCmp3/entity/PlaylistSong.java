package com.example.CMCmp3.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistSong {

    @EmbeddedId
    private PlaylistSongId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playlistId") // Map với field playlistId trong khóa chính
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("songId") // Map với field songId trong khóa chính
    @JoinColumn(name = "song_id")
    private Song song;

    // ĐÂY LÀ LÝ DO CHÚNG TA PHẢI TẠO BẢNG NÀY:
    @Column(name = "song_order")
    private Integer order; // Thứ tự bài hát (1, 2, 3...)

    @CreationTimestamp
    private LocalDateTime addedAt; // Ngày thêm bài vào playlist
}
