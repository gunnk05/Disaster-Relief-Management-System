package com.relieflink.user;

import com.relieflink.common.exception.ResourceNotFoundException;
import com.relieflink.user.dto.UpdateProfileRequest;
import com.relieflink.user.dto.UserListResponse;
import com.relieflink.user.dto.UserProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        return UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setContactNumber(request.getContactNumber());

        userRepository.save(user);

        return UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserListResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserListResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .contactNumber(user.getContactNumber())
                        .build())
                .collect(Collectors.toList());
    }
}
