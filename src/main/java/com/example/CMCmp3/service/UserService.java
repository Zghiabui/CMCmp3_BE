package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.RegisterDTO;
import com.example.CMCmp3.dto.UserDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* ========= REGISTER / AUTH ========= */

    @Transactional
    public User registerUser(RegisterDTO registerDTO) {
        // normalize
        final String email = safeLower(registerDTO.getEmail());
        final String displayName = safeTrim(registerDTO.getDisplayName());
        final String phone = safeTrim(registerDTO.getPhone());
        final String avatarUrl = safeTrim(registerDTO.getAvatarUrl());

        // email bắt buộc
        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email không được để trống");
        }

        // phone (nếu có) thì phải duy nhất
        if (StringUtils.hasText(phone) && userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        var existingOpt = userRepository.findByEmailIgnoreCase(email);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();

            if (existing.getStatus() == UserStatus.ACTIVE) {
                throw new RuntimeException("Email đã được sử dụng.");
            }

            // Khôi phục tài khoản DEACTIVE
            updateUserFromDTO(existing, displayName, email, phone, avatarUrl,
                    registerDTO.getDob(), registerDTO.getGender());
            existing.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            existing.setStatus(UserStatus.ACTIVE);
            existing.setRole(Role.USER);
            existing.setProvider(AuthProvider.LOCAL);

            return userRepository.save(existing);
        }

        // Tạo mới
        String username = generateUniqueUsername(displayName);
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .provider(AuthProvider.LOCAL)
                .build();

        updateUserFromDTO(user, displayName, email, phone, avatarUrl,
                registerDTO.getDob(), registerDTO.getGender());

        return userRepository.save(user);
    }

    public User authenticate(String emailRaw, String rawPassword) {
        String email = safeLower(emailRaw);
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

    /* ========= ADMIN / LIST ========= */

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll(org.springframework.data.domain.Sort.by("id").ascending())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public void updatePhone(Long userId, String newPhone) {
        String phone = safeTrim(newPhone);
        if (StringUtils.hasText(phone) && userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        u.setPhone(phone);
        userRepository.save(u);
    }

    /* ========= HELPERS ========= */

    private void updateUserFromDTO(
            User user,
            String displayName,
            String email,
            String phone,
            String avatarUrl,
            String dob,
            String gender
    ) {
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAvatarUrl(avatarUrl);

        if (StringUtils.hasText(dob)) {
            try {
                user.setDob(LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (Exception ignored) {}
        }

        if (StringUtils.hasText(gender)) {
            try {
                user.setGender(Gender.valueOf(gender.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private String generateUniqueUsername(String base) {
        String cleanBase = safeTrim(base).replaceAll("[^\\p{L}0-9]", "").toLowerCase();
        cleanBase = StringUtils.hasText(cleanBase) ? cleanBase : "user";
        String username;
        do {
            String suffix = UUID.randomUUID().toString().substring(0, 6);
            username = cleanBase + suffix;
        } while (userRepository.existsByUsername(username));
        return username;
    }

    private UserDTO toDTO(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setDisplayName(u.getDisplayName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());               // ✅ phone cho FE
        dto.setRole(u.getRole().name());
        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }

    private static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static String safeLower(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }
}
