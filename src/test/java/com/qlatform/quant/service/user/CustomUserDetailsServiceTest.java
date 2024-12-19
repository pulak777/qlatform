package com.qlatform.quant.service.user;

import com.qlatform.quant.model.User;
import com.qlatform.quant.model.adapter.CustomUserDetails;
import com.qlatform.quant.model.authentication.AuthProvider;
import com.qlatform.quant.model.authentication.Role;
import com.qlatform.quant.repository.userdb.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .email(TEST_EMAIL)
                .name("Test User")
                .password("hashedPassword")
                .provider(AuthProvider.LOCAL)
                .role(Role.USER)
                .emailVerified(true)
                .enabled(true)
                .build();
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(TEST_EMAIL)
        );

        assertEquals("User not found with email: " + TEST_EMAIL, exception.getMessage());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsername_NullEmail() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null)
        );
    }

    @Test
    void loadUserByUsername_DisabledUser() {
        testUser.setEnabled(false);
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        assertFalse(userDetails.isEnabled());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsername_BlockedUser() {
        testUser.setEnabled(false);
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        assertFalse(userDetails.isEnabled());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void loadUserByUsername_AdminUser() {
        testUser.setRole(Role.ADMIN);
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
        verify(userRepository).findByEmail(TEST_EMAIL);
    }
}