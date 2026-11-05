package com.relieflink.auth;

import com.relieflink.auth.dto.AuthResponse;
import com.relieflink.auth.dto.LoginRequest;
import com.relieflink.auth.dto.RegisterRequest;

/**
 * Service interface for authentication operations.
 *
 * <p><b>Interface Segregation:</b> Auth operations are separated from
 * user profile operations ({@link com.relieflink.user.UserService}).</p>
 *
 * <p><b>Dependency Inversion:</b> {@link com.relieflink.auth.AuthController}
 * depends on this interface, not the concrete implementation.</p>
 */
public interface AuthService {

    AuthResponse login(LoginRequest request);

    void register(RegisterRequest request);
}
