package com.example.CMCmp3.security;

import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        // ⚡ Nhớ convert role -> ROLE_...
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + u.getRole().name())
        );

        // ⚡ Trả về UserDetails chuẩn, KHÔNG return entity User nữa
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())      // username = email
                .password(u.getPassword())       // mật khẩu đã BCrypt
                .authorities(authorities)         // quyền
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
