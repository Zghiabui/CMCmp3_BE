package com.example.CMCmp3.security;

import com.example.CMCmp3.entity.AuthProvider;
import com.example.CMCmp3.entity.Role;
import com.example.CMCmp3.entity.User;
import com.example.CMCmp3.entity.UserStatus;
import com.example.CMCmp3.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String avatarUrl = (String) attributes.get("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update user info if necessary
            user.setDisplayName(name);
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
        } else {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // Use email as username
            user.setDisplayName(name);
            user.setAvatarUrl(avatarUrl);
            user.setProvider(AuthProvider.GOOGLE);
            user.setPassword(""); // Set an empty password for OAuth2 users
            user.setRole(Role.USER);
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
