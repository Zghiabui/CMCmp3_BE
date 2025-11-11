package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.RegisterDTO;
import com.example.CMCmp3.dto.UpdateUserDTO;
import com.example.CMCmp3.dto.UserDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
import java.util.stream.Collectors;

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
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

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

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new RuntimeException("User not authenticated");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserDTO mapToUserDTO(User user) {
        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getGender(),
                user.getPhone(),
                user.getAvatarUrl(),
                roles
        );
    }

    public UserDTO getMe(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return mapToUserDTO(user);
    }

    public UserDTO updateMe(Authentication authentication, UpdateUserDTO userDTO) {
        User user = getCurrentUser(authentication);
        user.setDisplayName(userDTO.getDisplayName());
        user.setGender(userDTO.getGender());
        user.setPhone(userDTO.getPhoneNumber());
        User updatedUser = userRepository.save(user);
        return mapToUserDTO(updatedUser);
    }

    public UserDTO updateAvatar(Authentication authentication, MultipartFile file) {
        User user = getCurrentUser(authentication);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            String newFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/") // Assuming you have a controller to serve images from this path
                    .path(newFileName)
                    .toUriString();

            user.setAvatarUrl(fileDownloadUri);
            User updatedUser = userRepository.save(user);
            return mapToUserDTO(updatedUser);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}