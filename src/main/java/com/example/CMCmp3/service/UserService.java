package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.RegisterDTO;
import com.example.CMCmp3.dto.UpdateUserDTO;
import com.example.CMCmp3.dto.UserDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;   // 👈 THÊM LẠI IMPORT NÀY

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;
    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            System.out.println("✅ Avatar upload dir: " + this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    private UserDTO convertToDTO(User u) {
        return new UserDTO(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getGender(),
                u.getPhone(),
                u.getAvatarUrl(),
                Set.of(u.getRole().name())
        );
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new RuntimeException("User not authenticated");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User registerUser(RegisterDTO registerDTO) {
        final String email = safeLower(registerDTO.getEmail());
        final String displayName = safeTrim(registerDTO.getDisplayName());
        final String phone = safeTrim(registerDTO.getPhone());
        final String avatarUrl = safeTrim(registerDTO.getAvatarUrl());

        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email không được để trống");
        }

        var existingOpt = userRepository.findByEmailIgnoreCase(email);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            if (existing.getStatus() == UserStatus.ACTIVE) {
                throw new RuntimeException("Email đã được đăng ký");
            }
            if (StringUtils.hasText(phone)
                    && !phone.equals(existing.getPhone())
                    && userRepository.existsByPhone(phone)) {
                throw new RuntimeException("Số điện thoại đã được sử dụng");
            }
            updateUserFromDTO(existing, displayName, email, phone, avatarUrl,
                    registerDTO.getDob(), registerDTO.getGender());
            existing.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            existing.setStatus(UserStatus.ACTIVE);
            existing.setRole(Role.USER);
            existing.setProvider(AuthProvider.LOCAL);
            return userRepository.save(existing);
        }

        if (StringUtils.hasText(phone) && userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

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

    public UserDTO getMe(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return convertToDTO(user);
    }

    public UserDTO updateMe(Authentication authentication, UpdateUserDTO userDTO) {
        User user = getCurrentUser(authentication);
        user.setDisplayName(userDTO.getDisplayName());
        user.setGender(userDTO.getGender());
        user.setPhone(userDTO.getPhoneNumber());
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public UserDTO updateAvatar(Authentication authentication, MultipartFile file) {
        User user = getCurrentUser(authentication);

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File avatar trống");
        }

        // 1. Làm sạch tên file gốc
        String originalFileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename(), "Filename is null")
        );

        if (originalFileName.contains("..")) {
            throw new RuntimeException("Filename contains invalid path sequence " + originalFileName);
        }

        try {
            // 2. Sinh tên file mới (UUID + extension)
            String ext = "";
            int dotIdx = originalFileName.lastIndexOf('.');
            if (dotIdx != -1) {
                ext = originalFileName.substring(dotIdx); // ".jpg", ".png", ...
            }
            String newFileName = UUID.randomUUID() + ext;

            // 3. Đảm bảo thư mục tồn tại
            Files.createDirectories(this.fileStorageLocation);

            // 4. Copy file vào thư mục uploads
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✅ Saved avatar to: " + targetLocation.toAbsolutePath());

            // 5. Tạo URL đầy đủ cho FE (http://localhost:8082/images/...)
            String avatarUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/images/")
                    .path(newFileName)
                    .toUriString();

            user.setAvatarUrl(avatarUrl);
            User updatedUser = userRepository.save(user);

            return convertToDTO(updatedUser);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
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

    private void updateUserFromDTO(
            User user, String displayName, String email, String phone,
            String avatarUrl, String dob, String gender) {
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

    private static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static String safeLower(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }
}
