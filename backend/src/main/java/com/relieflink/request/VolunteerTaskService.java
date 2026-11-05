package com.relieflink.request;

import com.relieflink.request.dto.*;
import com.relieflink.resource.dto.ResourceResponse;

import java.util.List;

/**
 * Service interface for volunteer task operations.
 *
 * <p><b>Interface Segregation Principle:</b> Volunteer-specific operations
 * are separated from citizen and admin operations.</p>
 */
public interface VolunteerTaskService {

    List<PendingRequestResponse> getPendingRequests();

    void assignTask(Long volunteerId, Long requestId);

    void completeTask(Long volunteerId, Long requestId);

    List<VolunteerTaskResponse> getMyTasks(Long volunteerId);

    List<ResourceResponse> getVolunteerResources();
}
