// src/main/java/com/example/CMCmp3/dto/TopPlaylistDTO.java
package com.example.CMCmp3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopPlaylistDTO {
    private String id;
    private String name;
    private String imageUrl;
    private Long listenCount;
    private String creatorDisplayName;
}
