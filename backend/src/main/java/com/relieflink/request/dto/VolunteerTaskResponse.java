package com.relieflink.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerTaskResponse {
    private Long id;
    private Long requestId;
    private String status;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private Long citizenId;
    private String citizen;
    private String resource;
    private Integer quantityRequested;
    private String location;
    private String remarks;
}
