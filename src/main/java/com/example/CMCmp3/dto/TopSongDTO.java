package com.example.CMCmp3.dto;

import com.example.CMCmp3.entity.Artist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSongDTO {
    private String id;
    private String title;
    private String artist;
    private String imageUrl;
    private Long listenCount;

    public TopSongDTO(String id, String title, Artist artist, String imageUrl, Long listenCount) {
        this.id = id;
        this.title = title;
        this.artist = artist.getName();
        this.imageUrl = imageUrl;
        this.listenCount = listenCount;
    }
}
