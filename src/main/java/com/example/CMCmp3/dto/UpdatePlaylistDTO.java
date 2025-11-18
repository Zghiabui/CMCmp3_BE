package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePlaylistDTO {
    @NotBlank(message = "Tên playlist không được để trống")
    private String name;

    @NotBlank(message = "Chế độ riêng tư không được để trống")
    @Pattern(regexp = "PUBLIC|PRIVATE", message = "Chế độ riêng tư phải là 'PUBLIC' hoặc 'PRIVATE'")
    private String privacy;
}
