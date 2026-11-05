package com.relieflink.auth;

import com.relieflink.auth.dto.AuthResponse;
import com.relieflink.auth.dto.LoginRequest;
import com.relieflink.auth.dto.RegisterRequest;
import com.relieflink.common.enums.Role;
import com.relieflink.common.exception.BadRequestException;
import com.relieflink.common.exception.UnauthorizedException;
import com.relieflink.config.JwtTokenProvider;
import com.relieflink.user.User;
import com.relieflink.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("$2a$12$hashedpassword")
                .role(Role.citizen)
                .build();
    }

    @Test
    @DisplayName("login() should return token and user info for valid credentials")
    void login_ValidCredentials_ReturnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("correctPassword");
        request.setRole("citizen");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("correctPassword", testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "test@example.com", "citizen"))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUser().getId()).isEqualTo(1L);
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getUser().getRole()).isEqualTo("citizen");
    }

    @Test
    @DisplayName("login() should throw UnauthorizedException for wrong password")
    void login_WrongPassword_ThrowsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");
        request.setRole("citizen");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid password.");
    }

    @Test
    @DisplayName("login() should throw UnauthorizedException for wrong role")
    void login_WrongRole_ThrowsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("correctPassword");
        request.setRole("admin");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("correctPassword", testUser.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("not authorized as admin");
    }

    @Test
    @DisplayName("login() should throw UnauthorizedException for non-existent user")
    void login_UserNotFound_ThrowsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password");
        request.setRole("citizen");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User not found.");
    }

    @Test
    @DisplayName("register() should save user with encoded password")
    void register_ValidRequest_SavesUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setRole("citizen");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$encoded");

        authService.register(request);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("newuser") &&
                user.getPassword().equals("$2a$12$encoded") &&
                user.getRole() == Role.citizen
        ));
    }

    @Test
    @DisplayName("register() should throw BadRequestException for duplicate email")
    void register_DuplicateEmail_ThrowsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setRole("citizen");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("email already exists");
    }
}
