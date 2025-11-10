package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.Instant;

@Data
public class SongDTO {

    private String id;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nghệ sĩ không được để trống")
    private String artist;

    private String imageUrl;

    @NotBlank(message = "Đường dẫn file không được để trống")
    private String filePath;

    private Long listenCount;

    private Long likeCount;

    private String description;

    private String label;

    private Instant createdAt;
}
