package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.ChangePasswordDTO;
import com.example.CMCmp3.dto.UpdateUserDTO;
import com.example.CMCmp3.dto.UserDTO;
import com.example.CMCmp3.service.UserService;
import jakarta.validation.Valid;
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

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(Authentication authentication) {
        // Sử dụng hàm getMe đã có trong UserService
        return ResponseEntity.ok(userService.getMe(authentication));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyInfo(Authentication authentication, @RequestBody UpdateUserDTO userDTO) {
        return ResponseEntity.ok(userService.updateMe(authentication, userDTO));
    }

    @PatchMapping("/me/avatar")
    public ResponseEntity<UserDTO> updateMyAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(authentication, file));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(authentication, dto);
        return ResponseEntity.ok().build();
    }

}