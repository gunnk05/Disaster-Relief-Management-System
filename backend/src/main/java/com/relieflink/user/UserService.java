package com.relieflink.user;

import com.relieflink.user.dto.UpdateProfileRequest;
import com.relieflink.user.dto.UserListResponse;
import com.relieflink.user.dto.UserProfileResponse;

import java.util.List;

/**
 * Service interface for user profile operations.
 *
 * <p><b>Interface Segregation:</b> Profile operations are separated
 * from authentication ({@link com.relieflink.auth.AuthService}).</p>
 */
public interface UserService {

    UserProfileResponse getUserProfile(Long id);

    UserProfileResponse updateProfile(Long id, UpdateProfileRequest request);

    List<UserListResponse> getAllUsers();
}
