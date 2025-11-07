package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class PlaylistDTO {

    private Long id;

    @NotBlank(message = "Tên playlist không được để trống")
    private String name;
    private String description;
    private String imageUrl;
    private Set<Long> songIds;
    private int numberOfSongs;
    private Long listenCount;
    private Long likeCount;
    private Long userId;
}
