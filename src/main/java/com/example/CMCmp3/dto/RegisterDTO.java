package com.example.CMCmp3.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "Tên hiển thị không được để trống")
    @Pattern(regexp = "^[\\p{L}0-9 ]+$", message = "Tên hiển thị không được chứa ký tự đặc biệt")
    private String displayName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email phải có định dạng @gmail.com")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có đúng 10 chữ số")
    private String phone;

    private String avatarUrl;

    private String dob;

    private String gender;
}