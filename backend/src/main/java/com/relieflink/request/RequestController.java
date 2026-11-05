package com.relieflink.request;

import com.relieflink.common.ApiResponse;
import com.relieflink.request.dto.*;
import com.relieflink.resource.dto.ResourceResponse;
import com.relieflink.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller consolidating citizen, volunteer, and admin request endpoints.
 *
 * <p><b>Dependency Inversion:</b> Depends on three service interfaces
 * ({@link CitizenRequestService}, {@link VolunteerTaskService},
 * {@link AdminRequestService}), not the concrete implementation.</p>
 */
@RestController
public class RequestController {

    private final CitizenRequestService citizenRequestService;
    private final VolunteerTaskService volunteerTaskService;
    private final AdminRequestService adminRequestService;

    public RequestController(CitizenRequestService citizenRequestService,
                             VolunteerTaskService volunteerTaskService,
                             AdminRequestService adminRequestService) {
        this.citizenRequestService = citizenRequestService;
        this.volunteerTaskService = volunteerTaskService;
        this.adminRequestService = adminRequestService;
    }

    // =========================================================================
    // CITIZEN ENDPOINTS
    // =========================================================================

    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<Void>> createRequest(
            @Valid @RequestBody CreateRequestDto dto,
            @AuthenticationPrincipal User currentUser) {
        citizenRequestService.createRequest(currentUser.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Request submitted successfully."));
    }

    @GetMapping("/requests/my")
    public ResponseEntity<List<RequestResponse>> getMyRequests(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(citizenRequestService.getMyRequests(currentUser.getId()));
    }

    // =========================================================================
    // VOLUNTEER ENDPOINTS
    // =========================================================================

    @GetMapping("/volunteers/requests")
    public ResponseEntity<List<PendingRequestResponse>> getVolunteerRequests() {
        return ResponseEntity.ok(volunteerTaskService.getPendingRequests());
    }

    @PostMapping("/volunteers/assign-task")
    public ResponseEntity<ApiResponse<Void>> assignTask(
            @Valid @RequestBody AssignTaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (!currentUser.getId().equals(request.getVolunteerId())) {
            throw new AccessDeniedException("You can only assign tasks to yourself.");
        }
        volunteerTaskService.assignTask(request.getVolunteerId(), request.getRequestId());
        return ResponseEntity.ok(ApiResponse.success("Task assigned successfully"));
    }

    @PostMapping("/volunteers/complete-task")
    public ResponseEntity<ApiResponse<Void>> completeTask(
            @Valid @RequestBody CompleteTaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (!currentUser.getId().equals(request.getVolunteerId())) {
            throw new AccessDeniedException("You can only complete your own tasks.");
        }
        volunteerTaskService.completeTask(request.getVolunteerId(), request.getRequestId());
        return ResponseEntity.ok(
                ApiResponse.success("Task marked as completed. Waiting for admin verification."));
    }

    @GetMapping("/volunteers/my-tasks")
    public ResponseEntity<List<VolunteerTaskResponse>> getMyTasks(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(volunteerTaskService.getMyTasks(currentUser.getId()));
    }

    @GetMapping("/volunteers/resources")
    public ResponseEntity<List<ResourceResponse>> getVolunteerResources() {
        return ResponseEntity.ok(volunteerTaskService.getVolunteerResources());
    }

    // =========================================================================
    // ADMIN ENDPOINTS
    // =========================================================================

    @GetMapping("/admin/requests")
    public ResponseEntity<List<AdminRequestResponse>> getAdminRequests() {
        return ResponseEntity.ok(adminRequestService.getAllRequestsWithVolunteerInfo());
    }

    @PutMapping("/admin/requests/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        adminRequestService.updateRequestStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Request status updated successfully"));
    }
}
