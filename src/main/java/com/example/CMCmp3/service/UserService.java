package com.example.CMCmp3.service;

import com.yourcompany.musicapp.entity.User;
import com.yourcompany.musicapp.enums.Role;
import com.yourcompany.musicapp.enums.UserStatus;
import com.yourcompany.musicapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String displayName, String email, String rawPassword) {
        // Kiểm tra nếu email đã tồn tại
        var existingUserOpt = userRepository.findByEmailIgnoreCase(email);

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.getStatus() == UserStatus.ACTIVE) {
                throw new RuntimeException("Email đã được sử dụng.");
            }

            // Nếu tài khoản DEACTIVE → khôi phục
            existingUser.setDisplayName(displayName);
            existingUser.setPassword(passwordEncoder.encode(rawPassword));
            existingUser.setStatus(UserStatus.ACTIVE);
            existingUser.setRole(Role.USER);
            return userRepository.save(existingUser);
        }

        // Tạo tài khoản mới
        String generatedUsername = generateUniqueUsername(displayName);

        User newUser = User.builder()
                .username(generatedUsername)
                .displayName(displayName)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(newUser);
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
}