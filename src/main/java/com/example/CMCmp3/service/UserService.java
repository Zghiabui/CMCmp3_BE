package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.RegisterDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterDTO registerDTO) {
        // Kiểm tra nếu email đã tồn tại
        var existingUserOpt = userRepository.findByEmailIgnoreCase(registerDTO.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.getStatus() == UserStatus.ACTIVE) {
                throw new RuntimeException("Email đã được sử dụng.");
            }

            // Nếu tài khoản DEACTIVE → khôi phục và cập nhật
            updateUserFromDTO(existingUser, registerDTO);
            existingUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            existingUser.setStatus(UserStatus.ACTIVE);
            existingUser.setRole(Role.USER);
            existingUser.setProvider(AuthProvider.LOCAL); // ⬅ ADDED
            return userRepository.save(existingUser);
        }

        // Tạo tài khoản mới
        String generatedUsername = generateUniqueUsername(registerDTO.getDisplayName());

        User newUser = User.builder()
                .username(generatedUsername)
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .provider(AuthProvider.LOCAL) // ⬅ ADDED
                .build();

        updateUserFromDTO(newUser, registerDTO);

        return userRepository.save(newUser);
    }

    private void updateUserFromDTO(User user, RegisterDTO dto) {
        user.setDisplayName(dto.getDisplayName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAvatarUrl(dto.getAvatarUrl());

        if (StringUtils.hasText(dto.getDob())) {
            try {
                user.setDob(LocalDate.parse(dto.getDob(), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (Exception e) {
                // Xử lý lỗi nếu định dạng ngày không hợp lệ, có thể log hoặc bỏ qua
            }
        }

        if (StringUtils.hasText(dto.getGender())) {
            try {
                user.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Xử lý nếu giá trị gender không hợp lệ
            }
        }
    }

    private String generateUniqueUsername(String base) {
        String cleanBase = base.replaceAll("[^\\p{L}0-9]", "").toLowerCase();
        String username;
        do {
            String suffix = UUID.randomUUID().toString().substring(0, 6);
            username = cleanBase + suffix;
        } while (userRepository.existsByUsername(username));
        return username;
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        return user;
    }
}