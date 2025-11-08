package com.example.CMCmp3.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PlaylistDTO {

    // id của Playlist là String (UUID)
    private String id;

    @NotBlank(message = "Tên playlist không được để trống")
    private String name;

    private String description;
    private String imageUrl;

    // Song.id là String -> giữ Set<String>
    private Set<String> songs;

    private int numberOfSongs;

    private Long listenCount;
    private Long likeCount;

    private Long userId;

    // LocalDateTime -> nên dùng ISO
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
