package com.relieflink.user;

import com.relieflink.common.ApiResponse;
import com.relieflink.user.dto.UpdateProfileRequest;
import com.relieflink.user.dto.UserListResponse;
import com.relieflink.user.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user profile and admin user management.
 */
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        enforceOwnershipOrAdmin(id, currentUser);
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/users/{id}/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User currentUser) {
        enforceOwnershipOrAdmin(id, currentUser);
        userService.updateProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    private void enforceOwnershipOrAdmin(Long targetId, User currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !currentUser.getId().equals(targetId)) {
            throw new AccessDeniedException("You can only access your own profile.");
        }
    }
}
