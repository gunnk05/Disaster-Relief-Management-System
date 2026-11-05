package com.relieflink.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponse {
    private Long id;
    private String status;
    private Integer quantityRequested;
    private String remarks;
    private String resource;
    private String location;
}
