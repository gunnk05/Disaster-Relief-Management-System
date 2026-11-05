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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Concrete implementation of {@link AuthService}.
 *
 * <p><b>Single Responsibility:</b> Handles only authentication logic
 * (login, registration). Profile management is in
 * {@link com.relieflink.user.UserServiceImpl}.</p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("User not found."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password.");
        }

        Role requestedRole;
        try {
            requestedRole = Role.valueOf(request.getRole().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + request.getRole());
        }

        if (user.getRole() != requestedRole) {
            throw new UnauthorizedException(
                    "User is not authorized as " + request.getRole() + ".");
        }

        String token = jwtTokenProvider.generateToken(
                user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .message("Login successful")
                .token(token)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        Role role;
        try {
            role = Role.valueOf(request.getRole().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid role. Allowed roles: admin, volunteer, citizen.");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("An account with this email already exists.");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new BadRequestException("Username is already taken.");
        }

        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail().trim().toLowerCase())
                .role(role)
                .contactNumber(request.getContactNumber())
                .isRequestingHelp(false)
                .build();

        userRepository.save(user);
    }
}
