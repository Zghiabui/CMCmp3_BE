package com.example.CMCmp3.service;

import com.example.CMCmp3.dto.PlaylistDTO;
import com.example.CMCmp3.entity.*;
import com.example.CMCmp3.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private PlaylistLikeRepository playlistLikeRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private FirebaseStorageService firebaseStorageService;
    
    @Mock
    private SongService songService;


    @InjectMocks
    private PlaylistService playlistService;

    private User owner;
    private User otherUser;
    private User adminUser;
    private Playlist privatePlaylist;
    private Playlist publicPlaylist;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setRole(Role.USER);
        owner.setDisplayName("Owner");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");
        otherUser.setRole(Role.USER);
        otherUser.setDisplayName("Other");


        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);
        adminUser.setDisplayName("Admin");


        privatePlaylist = new Playlist();
        privatePlaylist.setId(101L);
        privatePlaylist.setPrivacy(PlaylistPrivacy.PRIVATE);
        privatePlaylist.setOwner(owner);
        privatePlaylist.setPlaylistSongs(new HashSet<>());
        privatePlaylist.setArtists(new HashSet<>());
        privatePlaylist.setLikes(new HashSet<>());


        publicPlaylist = new Playlist();
        publicPlaylist.setId(102L);
        publicPlaylist.setPrivacy(PlaylistPrivacy.PUBLIC);
        publicPlaylist.setOwner(owner);
        publicPlaylist.setPlaylistSongs(new HashSet<>());
        publicPlaylist.setArtists(new HashSet<>());
        publicPlaylist.setLikes(new HashSet<>());
    }

    private void mockSecurityContext(User user) {
        if (user == null) {
            SecurityContextHolder.clearContext();
            return;
        }
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void getById_ShouldThrowAccessDenied_WhenUserAccessesOthersPrivatePlaylist() {
        // Given
        mockSecurityContext(otherUser);
        when(playlistRepository.findById(privatePlaylist.getId())).thenReturn(Optional.of(privatePlaylist));

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            playlistService.getById(privatePlaylist.getId());
        });
        assertEquals("You are not authorized to access this playlist.", exception.getMessage());
    }

    @Test
    void getById_ShouldSucceed_WhenOwnerAccessesOwnPrivatePlaylist() {
        // Given
        mockSecurityContext(owner);
        when(playlistRepository.findById(privatePlaylist.getId())).thenReturn(Optional.of(privatePlaylist));

        // When
        PlaylistDTO result = playlistService.getById(privatePlaylist.getId());

        // Then
        assertNotNull(result);
        assertEquals(privatePlaylist.getId(), result.getId());
    }

    @Test
    void getById_ShouldSucceed_WhenAdminAccessesPrivatePlaylist() {
        // Given
        mockSecurityContext(adminUser);
        when(playlistRepository.findById(privatePlaylist.getId())).thenReturn(Optional.of(privatePlaylist));

        // When
        PlaylistDTO result = playlistService.getById(privatePlaylist.getId());

        // Then
        assertNotNull(result);
        assertEquals(privatePlaylist.getId(), result.getId());
    }

    @Test
    void getById_ShouldSucceed_WhenAnyUserAccessesPublicPlaylist() {
        // Given
        mockSecurityContext(otherUser);
        when(playlistRepository.findById(publicPlaylist.getId())).thenReturn(Optional.of(publicPlaylist));

        // When
        PlaylistDTO result = playlistService.getById(publicPlaylist.getId());

        // Then
        assertNotNull(result);
        assertEquals(publicPlaylist.getId(), result.getId());
    }

    @Test
    void getById_ShouldThrowAccessDenied_WhenUnauthenticatedUserAccessesPrivatePlaylist() {
        // Given
        when(playlistRepository.findById(privatePlaylist.getId())).thenReturn(Optional.of(privatePlaylist));

        // Mock the security context to represent an unauthenticated user
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenThrow(new RuntimeException("User not authenticated")); // Simulate no principal
        SecurityContextHolder.setContext(securityContext);


        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            playlistService.getById(privatePlaylist.getId());
        });
        assertEquals("You must be logged in to access this private playlist.", exception.getMessage());
    }
}