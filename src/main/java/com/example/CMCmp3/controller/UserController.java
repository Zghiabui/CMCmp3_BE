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

    private final UserService userService;

    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getMe(authentication));
    }

    // PUT /api/users/me
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyInfo(
            Authentication authentication,
            @RequestBody UpdateUserDTO userDTO
    ) {
        return ResponseEntity.ok(userService.updateMe(authentication, userDTO));
    }

    // 🔹 ĐỔI TỪ PATCH -> POST
    // 🔹 ĐỔI TÊN FIELD TỪ "file" -> "avatar" CHO KHỚP VỚI FE
    @PostMapping("/me/avatar")
    public ResponseEntity<UserDTO> updateMyAvatar(
            Authentication authentication,
            @RequestParam("avatar") MultipartFile avatar
    ) {
        return ResponseEntity.ok(userService.updateAvatar(authentication, avatar));
    }
}
