package com.relieflink.shelter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShelterRequest {

    @NotBlank(message = "Shelter name is required")
    private String name;

    @NotNull(message = "Location is required")
    private Long locationId;

    private Integer capacity;
    private Integer currentOccupancy;
    private String contactNumber;
}
