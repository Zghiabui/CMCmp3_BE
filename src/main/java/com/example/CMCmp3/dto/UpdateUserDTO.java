package com.example.CMCmp3.dto;

import com.example.CMCmp3.entity.Gender;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private String displayName;
    private Gender gender;
    private String phoneNumber;
}
