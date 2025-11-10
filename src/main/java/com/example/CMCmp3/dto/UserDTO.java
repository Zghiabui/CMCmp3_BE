package com.example.CMCmp3.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String displayName;
    private String email;
    private String phone;        // ✅ Thêm dòng này
    private String role;
    private LocalDateTime createdAt;
}
