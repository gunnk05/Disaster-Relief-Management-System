package com.relieflink.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterResponse {
    private Long id;
    private String name;
    private Long locationId;
    private String location;
    private Integer capacity;
    private Integer currentOccupancy;
    private String contactNumber;
}
