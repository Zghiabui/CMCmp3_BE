package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePlaylistDTO {
    @NotBlank(message = "Tên playlist không được để trống")
    private String title;

    private String description;
    private String imageUrl;
}