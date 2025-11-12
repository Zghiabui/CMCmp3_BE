package com.example.CMCmp3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Long songCount; // Số lượng bài hát (field phi chuẩn hóa để hiển thị nhanh)
}