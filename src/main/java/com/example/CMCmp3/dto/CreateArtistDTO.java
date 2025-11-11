package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateArtistDTO {

    @NotBlank(message = "Tên ca sĩ không được để trống")
    private String name;

    private String imageUrl; // Có thể null
}
