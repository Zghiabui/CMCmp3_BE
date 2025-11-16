package com.example.CMCmp3.dto;

import com.example.CMCmp3.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String displayName;
    private Gender gender;
    private String phoneNumber;
    private String avatarUrl;
    private Set<String> roles;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private java.time.LocalDateTime lastLoginTime;
}
