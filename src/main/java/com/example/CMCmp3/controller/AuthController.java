package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.LoginDTO;
import com.example.CMCmp3.dto.RegisterDTO;
import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.security.JwtService;
import com.example.CMCmp3.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO request) {
        try {
            User user = userService.registerUser(
                    request.getDisplayName(),
                    request.getEmail(),
                    request.getPassword()
            );
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng ký thành công",
                    "username", user.getUsername(),
                    "token", token
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {
        try {
            User user = userService.authenticate(request.getEmail(), request.getPassword());
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công",
                    "username", user.getUsername(),
                    "token", token
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
