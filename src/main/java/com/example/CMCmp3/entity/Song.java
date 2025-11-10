package com.example.CMCmp3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "songs")
public class Song {
    @Id
    private String id;
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    private Integer duration; // in seconds
    private String filePath;
    private String imageUrl;
    private Long listenCount;
    private Long likeCount;
    private Boolean isFavorite;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Instant createdAt;
    private String label;
}