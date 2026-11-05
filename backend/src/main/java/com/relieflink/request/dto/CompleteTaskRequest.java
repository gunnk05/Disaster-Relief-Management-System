package com.relieflink.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompleteTaskRequest {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Volunteer ID is required")
    private Long volunteerId;
}
