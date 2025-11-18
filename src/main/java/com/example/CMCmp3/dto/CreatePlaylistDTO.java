package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag; // Import for Pattern.Flag
import lombok.Data;

@Data
public class CreatePlaylistDTO {
    @NotBlank(message = "Tên playlist không được để trống")
    private String name; // Changed from title to name

    private String description;
    private String imageUrl;

    @NotBlank(message = "Chế độ riêng tư không được để trống")
    @Pattern(regexp = "PUBLIC|PRIVATE", flags = Flag.CASE_INSENSITIVE, message = "Chế độ riêng tư phải là 'PUBLIC' hoặc 'PRIVATE'")
    private String privacy; // 'public' hoặc 'private'
}