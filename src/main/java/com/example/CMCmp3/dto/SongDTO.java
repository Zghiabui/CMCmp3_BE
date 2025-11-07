package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SongDTO {

    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nghệ sĩ không được để trống")
    private String artist;

    @PositiveOrZero(message = "Thời lượng phải là số dương")
    private int duration;

    private String imageUrl;

    @NotBlank(message = "Đường dẫn file không được để trống")
    private String filePath;

    private Long listenCount;

    private Long likeCount;

    private String description;

    private String label;
}
