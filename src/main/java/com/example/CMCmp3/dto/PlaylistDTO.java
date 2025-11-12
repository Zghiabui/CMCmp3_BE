package com.example.CMCmp3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long playCount;
    private Long likeCount;
    private Long commentCount;
    private int songCount; // Số lượng bài hát trong playlist
    private String ownerName; // Tên người tạo
    private LocalDateTime createdAt;
}