package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTagDTO {

    @NotBlank(message = "Tên thể loại không được để trống")
    private String name;
    private String description;
}