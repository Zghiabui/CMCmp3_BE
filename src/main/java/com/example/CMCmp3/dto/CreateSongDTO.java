package com.example.CMCmp3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class CreateSongDTO {

    @NotBlank(message = "Tên bài hát không được để trống")
    private String title;

    @NotBlank(message = "File path không được để trống")
    private String filePath; // Giả sử FE đã upload file và gửi về đường dẫn/URL

    private String imageUrl;

    private String description;

    // Client gửi lên danh sách ID của Artist và Tag
    @NotEmpty(message = "Bài hát phải có ít nhất một ca sĩ")
    private Set<Long> artistIds;

    @NotEmpty(message = "Bài hát phải có ít nhất một thể loại")
    private Set<Long> tagIds;
}