package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SongsToPlaylistDTO {
    @NotEmpty(message = "Danh sách ID bài hát không được để trống")
    private List<Long> songIds;
}
