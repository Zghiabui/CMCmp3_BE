package com.example.CMCmp3.controller;

import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        User user = userRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Map.of(
                "id", user.getId(),
                "displayName", user.getDisplayName(),
                "avatarUrl", user.getAvatarUrl(),
                "email", ud.getUsername(),
                "roles", ud.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}
