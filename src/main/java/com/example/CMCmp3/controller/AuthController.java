package com.example.CMCmp3.controller;

import com.yourcompany.musicapp.dto.RegisterRequest;
import com.yourcompany.musicapp.entity.User;
import com.yourcompany.musicapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getDisplayName(),
                    request.getEmail(),
                    request.getPassword()
            );
            return ResponseEntity.ok("Đăng ký thành công với username: " + user.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
