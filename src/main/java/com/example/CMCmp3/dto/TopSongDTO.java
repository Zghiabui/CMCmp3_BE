package com.example.CMCmp3.dto;

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
}
