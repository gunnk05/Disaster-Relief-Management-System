package com.relieflink.auth;

import com.relieflink.auth.dto.AuthResponse;
import com.relieflink.auth.dto.LoginRequest;
import com.relieflink.auth.dto.RegisterRequest;
import com.relieflink.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 *
 * <p><b>Single Responsibility:</b> Only handles HTTP request/response
 * mapping. All business logic is delegated to {@link AuthService}.</p>
 *
 * <p><b>Dependency Inversion:</b> Depends on the {@link AuthService}
 * interface, not the concrete {@link AuthServiceImpl}.</p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT is stateless — client-side token removal is sufficient.
        // Server-side blacklisting can be added here if needed.
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully."));
    }
}
