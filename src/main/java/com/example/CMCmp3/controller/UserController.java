package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.UpdateUserDTO;
import com.example.CMCmp3.dto.UserDTO;
import com.example.CMCmp3.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // CHỈ inject UserService, không cần Repository
    private final UserService userService;

    /**
     * GET /api/users/me
     * Lấy thông tin cá nhân của user đang đăng nhập
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(Authentication authentication) {
        // Sử dụng hàm getMe đã có trong UserService
        return ResponseEntity.ok(userService.getMe(authentication));
    }

    /**
     * PUT /api/users/me
     * Cập nhật thông tin cá nhân
     */
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyInfo(Authentication authentication, @RequestBody UpdateUserDTO userDTO) {
        return ResponseEntity.ok(userService.updateMe(authentication, userDTO));
    }

    /**
     * PATCH /api/users/me/avatar
     * Cập nhật ảnh đại diện
     */
    @PatchMapping("/me/avatar")
    public ResponseEntity<UserDTO> updateMyAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(authentication, file));
    }

    // --- ĐÃ XÓA CÁC ENDPOINT ADMIN BỊ TRÙNG LẶP KHỎI FILE NÀY ---
}