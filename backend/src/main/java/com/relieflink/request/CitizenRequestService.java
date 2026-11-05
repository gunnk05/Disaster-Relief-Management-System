package com.relieflink.request;

import com.relieflink.request.dto.CreateRequestDto;
import com.relieflink.request.dto.RequestResponse;

import java.util.List;

/**
 * Service interface for citizen request operations.
 *
 * <p><b>Interface Segregation Principle:</b> Citizen-specific operations
 * are separated from volunteer and admin operations.</p>
 */
public interface CitizenRequestService {

    void createRequest(Long userId, CreateRequestDto dto);

    List<RequestResponse> getMyRequests(Long userId);
}
