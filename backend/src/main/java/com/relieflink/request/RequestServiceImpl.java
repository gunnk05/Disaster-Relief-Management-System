package com.relieflink.request;

import com.relieflink.audit.AuditLog;
import com.relieflink.audit.AuditLogRepository;
import com.relieflink.common.enums.RequestStatus;
import com.relieflink.common.enums.VolunteerRequestStatus;
import com.relieflink.common.exception.BadRequestException;
import com.relieflink.common.exception.ResourceNotFoundException;
import com.relieflink.request.dto.*;
import com.relieflink.resource.Resource;
import com.relieflink.resource.ResourceRepository;
import com.relieflink.resource.dto.ResourceResponse;
import com.relieflink.resource.Location;
import com.relieflink.resource.LocationRepository;
import com.relieflink.user.User;
import com.relieflink.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Unified implementation of all three request service interfaces.
 *
 * <p><b>Liskov Substitution:</b> This class can be used wherever any of
 * its interfaces ({@link CitizenRequestService}, {@link VolunteerTaskService},
 * {@link AdminRequestService}) is expected.</p>
 *
 * <p><b>Single Responsibility:</b> While implementing multiple interfaces,
 * each method group handles a distinct concern (citizen, volunteer, admin).</p>
 */
@Service
public class RequestServiceImpl
        implements CitizenRequestService, VolunteerTaskService, AdminRequestService {

    private final RequestRepository requestRepository;
    private final VolunteerRequestRepository volunteerRequestRepository;
    private final ResourceRepository resourceRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public RequestServiceImpl(RequestRepository requestRepository,
                              VolunteerRequestRepository volunteerRequestRepository,
                              ResourceRepository resourceRepository,
                              LocationRepository locationRepository,
                              UserRepository userRepository,
                              AuditLogRepository auditLogRepository) {
        this.requestRepository = requestRepository;
        this.volunteerRequestRepository = volunteerRequestRepository;
        this.resourceRepository = resourceRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // =========================================================================
    // CITIZEN OPERATIONS
    // =========================================================================

    @Override
    @Transactional
    public void createRequest(Long userId, CreateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", dto.getResourceId()));

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", dto.getLocationId()));

        Request request = Request.builder()
                .user(user)
                .resource(resource)
                .location(location)
                .quantityRequested(dto.getQuantityRequested())
                .status(RequestStatus.pending)
                .requestTime(LocalDateTime.now())
                .remarks(dto.getRemarks())
                .build();

        requestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestResponse> getMyRequests(Long userId) {
        return requestRepository.findAllByUserId(userId).stream()
                .map(r -> RequestResponse.builder()
                        .id(r.getId())
                        .status(r.getStatus().name())
                        .quantityRequested(r.getQuantityRequested())
                        .remarks(r.getRemarks())
                        .resource(r.getResource().getName())
                        .location(r.getLocation().getName())
                        .build())
                .collect(Collectors.toList());
    }

    // =========================================================================
    // VOLUNTEER OPERATIONS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<PendingRequestResponse> getPendingRequests() {
        return requestRepository.findAllByStatus(RequestStatus.pending).stream()
                .map(r -> PendingRequestResponse.builder()
                        .requestId(r.getId())
                        .citizen(r.getUser().getUsername())
                        .location(r.getLocation().getName())
                        .resource(r.getResource().getName())
                        .quantityRequested(r.getQuantityRequested())
                        .status(r.getStatus().name())
                        .remarks(r.getRemarks())
                        .requestTime(r.getRequestTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignTask(Long volunteerId, Long requestId) {
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new BadRequestException("Invalid volunteer."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", requestId));

        if (request.getStatus() != RequestStatus.pending) {
            throw new BadRequestException("Request is no longer pending.");
        }

        // Update request status
        request.setStatus(RequestStatus.assigned);
        requestRepository.save(request);

        // Create volunteer assignment
        VolunteerRequest vr = VolunteerRequest.builder()
                .volunteer(volunteer)
                .request(request)
                .status(VolunteerRequestStatus.assigned)
                .assignedAt(LocalDateTime.now())
                .build();
        volunteerRequestRepository.save(vr);

        // Audit log
        auditLogRepository.save(AuditLog.builder()
                .action("Volunteer " + volunteerId + " assigned to request " + requestId)
                .performedBy(volunteer)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public void completeTask(Long volunteerId, Long requestId) {
        VolunteerRequest vr = volunteerRequestRepository
                .findByRequestIdAndVolunteerIdAndStatus(
                        requestId, volunteerId, VolunteerRequestStatus.assigned)
                .orElseThrow(() -> new ResourceNotFoundException("Assigned task not found."));

        // Mark volunteer task completed
        vr.setStatus(VolunteerRequestStatus.completed);
        vr.setCompletedAt(LocalDateTime.now());
        volunteerRequestRepository.save(vr);

        // Mark main request completed
        Request request = vr.getRequest();
        request.setStatus(RequestStatus.completed);
        requestRepository.save(request);

        // Audit log
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", volunteerId));
        auditLogRepository.save(AuditLog.builder()
                .action("Volunteer " + volunteerId + " completed request " + requestId)
                .performedBy(volunteer)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VolunteerTaskResponse> getMyTasks(Long volunteerId) {
        return volunteerRequestRepository.findAllByVolunteerId(volunteerId).stream()
                .map(vr -> {
                    Request r = vr.getRequest();
                    return VolunteerTaskResponse.builder()
                            .id(vr.getId())
                            .requestId(r.getId())
                            .status(vr.getStatus().name())
                            .assignedAt(vr.getAssignedAt())
                            .completedAt(vr.getCompletedAt())
                            .citizenId(r.getUser().getId())
                            .citizen(r.getUser().getUsername())
                            .resource(r.getResource().getName())
                            .quantityRequested(r.getQuantityRequested())
                            .location(r.getLocation().getName())
                            .remarks(r.getRemarks())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getVolunteerResources() {
        return resourceRepository.findAllWithLocation().stream()
                .map(r -> ResourceResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .type(r.getType())
                        .quantity(r.getQuantity())
                        .unit(r.getUnit())
                        .locationName(r.getLocation() != null ? r.getLocation().getName() : null)
                        .lastUpdated(r.getLastUpdated())
                        .build())
                .collect(Collectors.toList());
    }

    // =========================================================================
    // ADMIN OPERATIONS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<AdminRequestResponse> getAllRequestsWithVolunteerInfo() {
        List<Request> requests = requestRepository.findAllWithDetails();

        return requests.stream()
                .map(r -> {
                    Optional<VolunteerRequest> vrOpt =
                            volunteerRequestRepository.findByRequestId(r.getId());
                    String volunteerName = vrOpt
                            .map(vr -> vr.getVolunteer().getUsername())
                            .orElse(null);

                    return AdminRequestResponse.builder()
                            .requestId(r.getId())
                            .citizen(r.getUser().getUsername())
                            .location(r.getLocation().getName())
                            .resource(r.getResource().getName())
                            .quantityRequested(r.getQuantityRequested())
                            .status(r.getStatus().name())
                            .remarks(r.getRemarks())
                            .volunteer(volunteerName)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRequestStatus(Long requestId, String status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", requestId));

        RequestStatus newStatus;
        try {
            newStatus = RequestStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }

        // If fulfilling a completed request, deduct resource stock
        if (newStatus == RequestStatus.fulfilled
                && request.getStatus() == RequestStatus.completed) {

            Resource resource = request.getResource();
            if (resource.getQuantity() < request.getQuantityRequested()) {
                throw new BadRequestException(
                        "Insufficient stock to fulfill this request.");
            }
            resource.setQuantity(resource.getQuantity() - request.getQuantityRequested());
            resourceRepository.save(resource);
        }

        // Delete volunteer assignment on status change (mirrors Node.js behavior)
        volunteerRequestRepository.deleteByRequestId(requestId);

        request.setStatus(newStatus);
        requestRepository.save(request);
    }
}
