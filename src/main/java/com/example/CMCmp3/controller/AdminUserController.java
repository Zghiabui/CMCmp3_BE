package com.example.CMCmp3.controller;

import com.example.CMCmp3.dto.UserDTO;
import com.example.CMCmp3.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /** GET /api/admin/users -> trả về danh sách người dùng (có phone) */
    @GetMapping
    public List<UserDTO> list() {
        return userService.getAllUsers();
    }

    /** PUT /api/admin/users/{id}/phone -> cập nhật số điện thoại */
    @PutMapping("/{id}/phone")
    public ResponseEntity<Void> updatePhone(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        userService.updatePhone(id, body.getOrDefault("phone", ""));
        return ResponseEntity.noContent().build();
    }
}
