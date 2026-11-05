package com.relieflink.request;

import com.relieflink.request.dto.AdminRequestResponse;

import java.util.List;

/**
 * Service interface for admin request operations.
 *
 * <p><b>Interface Segregation Principle:</b> Admin-specific operations
 * are separated from citizen and volunteer operations.</p>
 */
public interface AdminRequestService {

    List<AdminRequestResponse> getAllRequestsWithVolunteerInfo();

    void updateRequestStatus(Long requestId, String status);
}
