package com.example.CMCmp3.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Playlist {

    @Id
    private String id;

    private String name;
    private String description;
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> songs;

    private int numberOfSongs;
    private LocalDateTime createdAt;
    private int listenCount;
    private int likeCount;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
