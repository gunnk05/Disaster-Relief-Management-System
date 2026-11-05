package com.relieflink.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {
    private Long id;
    private String name;
    private String type;
    private Integer quantity;
    private String unit;
    private Long locationId;
    private String locationName;
    private LocalDateTime lastUpdated;
}
