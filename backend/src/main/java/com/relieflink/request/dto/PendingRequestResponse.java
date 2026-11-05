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
public class PendingRequestResponse {
    private Long requestId;
    private String citizen;
    private String location;
    private String resource;
    private Integer quantityRequested;
    private String status;
    private String remarks;
    private LocalDateTime requestTime;
}
